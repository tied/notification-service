package com.carlsberg.cx.notification.services.sms;

import static com.carlsberg.cx.notification.services.config.ApplicationConstants.MAILTRAP;
import static com.carlsberg.cx.notification.services.config.ApplicationConstants.TWILIO;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMPTY_USAGE_PROPERTY;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EXCEEDED_SMS_SEND_RETRIES;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.FAILED_SMS_SEND_ATTEMPT;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.INVALID_USAGE_PROPERTY;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.MISSING_EMAIL_CONFIGURATION;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.MISSING_SMS_CONFIGURATION;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.SmsConfig;
import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.data.repositories.SmsRepository;
import com.carlsberg.cx.notification.services.config.MessageConfigurationService;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.exceptions.SmsSendException;
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
public class SmsClientFactory {

  private final MailtrapSmsClient mailtrapSmsClient;
  private final TwillioSmsClient twillioSmsClient;
  private final MessageConfigurationService messageConfigurationService;
  private final ExecutorService executorService;
  private final SmsRepository smsRepository;

  @Value("${notification.retries.max}")
  private Integer maxRetries;

  @Value("${notification.delay.initial}")
  private Integer initialDelay;

  @Value("${notification.delay.max}")
  private Integer maxDelay;

  public void send(Sms sms) {

    //make sure there are remaining retries
    int remainingRetries = maxRetries - sms.getRetryAttempts();

    if (remainingRetries > 0){

      //create retry policy based on sms previous records
      RetryPolicy<Sms> retryPolicy = new RetryPolicy<Sms>()
          .onFailedAttempt(e -> increaseRetryCounter(sms))
          .onRetriesExceeded(e -> markSmsAsError(sms))
          .withMaxAttempts(remainingRetries)
          .handle(SmsSendException.class)
          .withBackoff(initialDelay, maxDelay, ChronoUnit.SECONDS);

      //empty fallback suppresses last exception
      Fallback<Sms> fallback = Fallback.of(() -> {});

      // Run with retry
      Failsafe.with(fallback, retryPolicy).with(executorService).run(() -> sendSms(sms));

    } else {

      //exceeded max retries
      markSmsAsError(sms);
    }
  }

  private void increaseRetryCounter(Sms sms) {
    int currentAttempt = sms.getRetryAttempts() + 1;
    log.error(String.format("%s - Failed attempt #%d to send sms: %s.",
        FAILED_SMS_SEND_ATTEMPT.getCode(), currentAttempt, sms.getSid()));
    sms.setRetryAttempts(currentAttempt);
    smsRepository.save(sms);
  }

  private void markSmsAsError(Sms sms) {
    log.error(String.format("%s - Max retries (%d) exceeded for sms: %s.",
        EXCEEDED_SMS_SEND_RETRIES.getCode(), maxRetries, sms.getSid()));
    sms.setStatus(MessageStatusEnum.ERROR);
    smsRepository.save(sms);
  }

  private void sendSms(Sms sms) throws SmsSendException {
    SmsConfig smsConfig;

    try {
      smsConfig = messageConfigurationService.fetchSmsConfig(
              sms.getFrom().getApplicationName(),
              sms.getFrom().getSubCode(),
              sms.getFrom().getSalesOrg());
    } catch (Exception e) {
      log.error(String.format("%s - Unable to obtain configurations for this sms: %s. %s",
          MISSING_SMS_CONFIGURATION.getCode(), sms.getSid(), e));
      throw new SmsSendException(sms);
    }

    if (smsConfig == null) {
      log.error(String.format("%s - Configuration missing for this sms: %s",
          MISSING_SMS_CONFIGURATION.getCode(), sms.getSid()));
      throw new SmsSendException(sms);
    }

    if (StringUtils.isEmpty(smsConfig.getUsage())) {
      log.error(String.format("%s - Unable to obtain 'usage' property for this sms: %s",
          EMPTY_USAGE_PROPERTY.getCode(), sms.getSid()));
      throw new SmsSendException(sms);
    }

    switch (smsConfig.getUsage()) {
      case MAILTRAP:
        mailtrapSmsClient.sendSms(sms, smsConfig);
        break;
      case TWILIO:
        twillioSmsClient.sendSms(sms, smsConfig);
        break;
      default:
        log.error(
            String.format(
                "%s - Invalid 'usage' property: '%s' for this sms: %s",
                INVALID_USAGE_PROPERTY.getCode(), smsConfig.getUsage(), sms.getSid()));
        throw new SmsSendException(sms);
    }
  }
}
