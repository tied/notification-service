package com.carlsberg.cx.notification.services.email;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.EmailConfig;
import com.carlsberg.cx.notification.data.documents.Recipient;
import com.carlsberg.cx.notification.data.repositories.EmailRepository;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.exceptions.EmailSendException;
import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SendGridEmailClient implements EmailClient {

  private final WebDataMapper webDataMapper;
  private final EmailRepository emailRepository;

  public void sendEmail(Email email, EmailConfig emailConfig) throws EmailSendException {
    if (email != null) {
      try {
        Mail mail = new Mail();
        mail.setFrom(new com.sendgrid.Email(emailConfig.getEmailAddress(), email.getFrom().getApplicationName()));
        mail.setSubject(email.getSubject());
        mail.addContent(new Content("text/html", email.getBody()));

        Personalization personalization = new Personalization();

        createPersonalization(email.getTo(), personalization::addTo);
        createPersonalization(email.getCc(), personalization::addCc);
        createPersonalization(email.getBcc(), personalization::addBcc);

        mail.addPersonalization(personalization);

        if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {

          // convert attachments and add to mail
          email.getAttachments().stream()
              .filter(Objects::nonNull)
              .map(webDataMapper::toAttachments)
              .forEach(mail::addAttachments);
        }

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = new SendGrid(emailConfig.getSendgridKey()).api(request);
        String emailResponse = response.getBody();

        log.info(
            String.format(
                "Status Code [%d] :: Body [%s] :: Headers [%s] :: Response [%s]",
                response.getStatusCode(),
                response.getBody(),
                response.getHeaders(),
                emailResponse));

        //update status
        email.setStatus(MessageStatusEnum.SENT);
        email.setSentAt(new Date());
        emailRepository.save(email);
        log.info("Sent Email to Twilio/Sendgrid: " + email.getEid());

      } catch (Exception e) {
        log.error(
            String.format(
                "Failed to send email to Twilio/Sendgrid: %s. %s ",
                email.getEid(), e.getMessage()));
        throw new EmailSendException(email);
      }
    }
  }

  /**
   * Create a personalization field (To, CC, BCC) based on a list of recipients and the correction
   * function.
   * @param recipients Recipient list must not be empty nor null.
   * @param function Consumer
   */
  private void createPersonalization(
      List<Recipient> recipients,
      Consumer<com.sendgrid.Email> function) {

    recipients.stream()
        .filter(Objects::nonNull)
        .map(recipient -> new com.sendgrid.Email(recipient.getEmailAddress(), recipient.getName()))
        .forEach(function);

  }
}
