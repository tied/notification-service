package com.carlsberg.cx.notification.scheduler;

import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.data.repositories.SmsRepository;
import com.carlsberg.cx.notification.services.sms.SmsClientFactory;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SmsScheduler {

  private final SmsRepository smsRepository;
  private final SmsClientFactory smsClientFactory;

  @Scheduled(fixedDelayString = "${notification.scheduler.sms.fixed-delay}")
  public void sendSms() {
    List<Sms> messages =
        smsRepository.findTop20ByStatusAndValidToIsNullOrderByScheduledAtAsc(
            MessageStatusEnum.SCHEDULED);

    if (messages != null && !messages.isEmpty()) {
      log.info(String.format("Sending '%d' sms", messages.size()));
      messages.parallelStream().filter(Objects::nonNull).forEach(smsClientFactory::send);
      log.info("sendSms() task completed.");
    }
  }
}
