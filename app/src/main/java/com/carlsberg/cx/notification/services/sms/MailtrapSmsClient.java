package com.carlsberg.cx.notification.services.sms;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.SmsConfig;
import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.data.repositories.SmsRepository;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.exceptions.SmsSendException;
import java.util.Date;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class MailtrapSmsClient implements SmsClient {

  private final WebDataMapper webDataMapper;
  private final SmsRepository smsRepository;

  public void sendSms(Sms sms, SmsConfig smsConfig) throws SmsSendException {
    if (sms != null) {

      try {
        // set up mailtrap sender
        JavaMailSender emailSender = webDataMapper.toJavaMailSender(smsConfig);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setFrom(smsConfig.getPhoneNumber() + "@notifications");
        helper.setTo(sms.getTo() + "@notifications");

        helper.setSubject(
            "[SMS - " + sms.getFrom().getApplicationName()
                + " - " + sms.getFrom().getSubCode()
                + " - " + sms.getFrom().getSalesOrg()
                + "] to: " + sms.getTo());

        helper.setText(
            String.format(
                "<html><head/><body>"
                    + "An Sms was sent with the following details:<br/>"
                    + "<b>Application:</b> %s <br/>"
                    + "<b>SubCode:</b> %s <br/>"
                    + "<b>SalesOrg:</b> %s <br/><br/>"
                    + "<b>From Phone Number:</b> %s <br/>"
                    + "<b>To Phone number:</b> %s<br/><br/>"
                    + "<b>Sms text:</b> %s"
                    + "</body></html>",
                sms.getFrom().getApplicationName(),
                sms.getFrom().getSubCode(),
                sms.getFrom().getSalesOrg(),
                smsConfig.getPhoneNumber(),
                sms.getTo(),
                sms.getBody()),
            true);

        // send message
        emailSender.send(message);

        // update status
        sms.setStatus(MessageStatusEnum.SENT);
        sms.setSentAt(new Date());
        smsRepository.save(sms);
        log.info("Sent Sms to Mailtrap: " + sms.getSid());

      } catch (Exception e) {
        log.error(
            String.format("Failed to send sms to Mailtrap: %s. %s ", sms.getSid(), e.getMessage()));
        throw new SmsSendException(sms);
      }
    }
  }
}
