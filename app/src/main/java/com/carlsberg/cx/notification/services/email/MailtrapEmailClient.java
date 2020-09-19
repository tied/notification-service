package com.carlsberg.cx.notification.services.email;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMAIL_ATTACHMENT_ERROR;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.EmailConfig;
import com.carlsberg.cx.notification.data.documents.Recipient;
import com.carlsberg.cx.notification.data.repositories.EmailRepository;
import com.carlsberg.cx.notification.web.config.NotificationExceptionEnum;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.exceptions.EmailSendException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MailtrapEmailClient implements EmailClient {

  private final WebDataMapper webDataMapper;
  private final EmailRepository emailRepository;

  public void sendEmail(Email email, EmailConfig emailConfig) throws EmailSendException {
    if (email != null) {

      try {
        JavaMailSender emailSender = webDataMapper.toJavaMailSender(emailConfig);

        boolean withAttachments =
            email.getAttachments() != null && !email.getAttachments().isEmpty();

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, withAttachments, "UTF-8");

        helper.setFrom(emailConfig.getEmailAddress());

        helper.setTo(
            email.getTo().stream()
                .map(Recipient::getEmailAddress)
                .collect(Collectors.toList())
                .toArray(new String[] {}));

        if (email.getCc() != null) {
          helper.setCc(
              email.getCc().stream()
                  .map(Recipient::getEmailAddress)
                  .collect(Collectors.toList())
                  .toArray(new String[] {}));
        }

        if (email.getBcc() != null) {
          helper.setBcc(
              email.getBcc().stream()
                  .map(Recipient::getEmailAddress)
                  .collect(Collectors.toList())
                  .toArray(new String[] {}));
        }

        helper.setSubject(
            "[EMAIL - "
                + email.getFrom().getApplicationName()
                + " - "
                + email.getFrom().getSubCode()
                + " - "
                + email.getFrom().getSalesOrg()
                + "] "
                + email.getSubject());

        helper.setText(email.getBody(), true);

        // add attachments
        if (withAttachments) {

          email.getAttachments().stream()
              .filter(Objects::nonNull)
              .forEach(
                  attach -> {
                    try {
                      if (AttachmentTypeEnum.INLINE
                          .toString()
                          .equalsIgnoreCase(attach.getDisposition())) {
                        helper.addInline(
                            attach.getAid(),
                            new ByteArrayResource(Base64.getDecoder().decode(attach.getData())),
                            attach.getType());

                      } else {
                        helper.addAttachment(
                            attach.getFilename(),
                            new ByteArrayResource(Base64.getDecoder().decode(attach.getData())));
                      }

                    } catch (MessagingException e) {
                      log.error(
                          String.format(
                              "%s - %s, %s ",
                              EMAIL_ATTACHMENT_ERROR.getCode(),
                              EMAIL_ATTACHMENT_ERROR.getName(),
                              e.getMessage()));
                    }
                  });
        }

        // send email
        emailSender.send(message);

        // update status
        email.setStatus(MessageStatusEnum.SENT);
        email.setSentAt(new Date());
        emailRepository.save(email);
        log.info("Sent Email to Mailtrap: " + email.getEid());

      } catch (Exception e) {
        log.error(
            String.format(
                "Failed to send email to Mailtrap: %s. %s ", email.getEid(), e.getMessage()));
        throw new EmailSendException(email);
      }
    }
  }
}
