package com.carlsberg.cx.notification.services.email;

import static com.carlsberg.cx.notification.services.config.ApplicationConstants.MAILTRAP;
import static com.carlsberg.cx.notification.services.config.ApplicationConstants.TWILIO;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMPTY_USAGE_PROPERTY;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EXCEEDED_EMAIL_SEND_RETRIES;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.FAILED_EMAIL_SEND_ATTEMPT;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.INVALID_USAGE_PROPERTY;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.MISSING_EMAIL_CONFIGURATION;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.EmailConfig;
import com.carlsberg.cx.notification.data.repositories.EmailRepository;
import com.carlsberg.cx.notification.services.config.MessageConfigurationService;
import com.carlsberg.cx.notification.web.config.NotificationExceptionEnum;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.exceptions.EmailSendException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Log4j2
public class EmailClientFactory {

  private final MailtrapEmailClient mailtrapEmailClient;
  private final SendGridEmailClient sendGridEmailClient;
  private final MessageConfigurationService messageConfigurationService;
  private final ExecutorService executorService;
  private final EmailRepository emailRepository;

  @Value("${notification.retries.max}")
  private Integer maxRetries;

  @Value("${notification.delay.initial}")
  private Integer initialDelay;

  @Value("${notification.delay.max}")
  private Integer maxDelay;

  public void send(Email email) {
    //make sure there are remaining retries
    int remainingRetries = maxRetries - email.getRetryAttempts();

    if (remainingRetries > 0){

      //create retry policy based on email's previous records
      RetryPolicy<Email> retryPolicy = new RetryPolicy<Email>()
          .onFailedAttempt(e -> increaseRetryCounter(email))
          .onRetriesExceeded(e -> markEmailAsError(email))
          .withMaxAttempts(remainingRetries)
          .handle(EmailSendException.class)
          .withBackoff(initialDelay, maxDelay, ChronoUnit.SECONDS);

      //empty fallback suppresses last exception
      Fallback<Email> fallback = Fallback.of(() -> {});

      // Run with retry
      Failsafe.with(fallback, retryPolicy).with(executorService).run(() -> sendEmail(email));

    } else {

      //exceeded max retries
      markEmailAsError(email);
    }
  }

  private void increaseRetryCounter(Email email) {
    int currentAttempt = email.getRetryAttempts() + 1;
    log.error(String.format("%s - Failed attempt #%d to send email: %s.",
        FAILED_EMAIL_SEND_ATTEMPT.getCode(), currentAttempt, email.getEid()));
    email.setRetryAttempts(currentAttempt);
    emailRepository.save(email);
  }

  private void markEmailAsError(Email email) {
    log.error(String.format("%s - Max retries (%d) exceeded for Email: %s.",
        EXCEEDED_EMAIL_SEND_RETRIES.getCode(), maxRetries, email.getEid()));
    email.setStatus(MessageStatusEnum.ERROR);
    emailRepository.save(email);
  }

  private void sendEmail(Email email) throws EmailSendException{
    EmailConfig emailConfig;

    try {
      emailConfig =
          messageConfigurationService.fetchEmailConfig(
              email.getFrom().getApplicationName(),
              email.getFrom().getSubCode(),
              email.getFrom().getSalesOrg());

    } catch (Exception e) {
      log.error(String.format("%s - Unable to obtain configurations for this email: %s. %s",
          MISSING_EMAIL_CONFIGURATION.getCode(), email.getEid(), e.getMessage()));
      throw new EmailSendException(email);
    }

    if (emailConfig == null) {
      log.error(String.format("%s - Configuration missing for this email: %s",
          MISSING_EMAIL_CONFIGURATION.getCode(), email.getEid()));
      throw new EmailSendException(email);
    }

    if (StringUtils.isEmpty(emailConfig.getUsage())) {
      log.error(String.format("%s - Unable to obtain 'usage' property for this email: %s ",
          EMPTY_USAGE_PROPERTY.getCode(), email.getEid()));
      throw new EmailSendException(email);
    }

    switch (emailConfig.getUsage()) {
      case MAILTRAP:
        mailtrapEmailClient.sendEmail(email, emailConfig);
        break;
      case TWILIO:
        sendGridEmailClient.sendEmail(email, emailConfig);
        break;
      default:
        log.error(
            String.format(
                "%s - Invalid 'usage' property: '%s' for this email: %s",
                INVALID_USAGE_PROPERTY.getCode(), emailConfig.getUsage(), email.getEid()));
        throw new EmailSendException(email);
    }
  }
}
