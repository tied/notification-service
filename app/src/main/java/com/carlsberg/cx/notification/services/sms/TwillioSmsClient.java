package com.carlsberg.cx.notification.services.sms;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.SmsConfig;
import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.data.repositories.SmsRepository;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.exceptions.SmsSendException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TwillioSmsClient implements SmsClient {

  private final SmsRepository smsRepository;

  public void sendSms(Sms sms, SmsConfig smsConfig) throws SmsSendException {
    if (sms != null) {

      try {
        Twilio.init(smsConfig.getAccountSid(), smsConfig.getAuthToken());

        // create and send twilio message
        Message message =
            new MessageCreator(
                new PhoneNumber(sms.getTo()),
                new PhoneNumber(smsConfig.getPhoneNumber()),
                sms.getBody())
                .create();

        log.info(
            String.format(
                "'%s' sent an sms from '%s' to '%s'.",
                sms.getFrom(), smsConfig.getPhoneNumber(), sms.getTo()));

        //update status
        sms.setStatus(MessageStatusEnum.SENT);
        sms.setSentAt(new Date());
        smsRepository.save(sms);
        log.info("Sent Sms to Twilio: " + sms.getSid());

      } catch (Exception e){
        log.error(
            String.format("Failed to send sms to Twilio: %s. %s ", sms.getSid(), e.getMessage()));
        throw new SmsSendException(sms);
      }
    }
  }
}
