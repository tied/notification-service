package com.carlsberg.cx.notification.scheduler;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.repositories.EmailRepository;
import com.carlsberg.cx.notification.services.email.EmailClientFactory;
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
public class EmailScheduler {

  private final EmailRepository emailRepository;
  private final EmailClientFactory emailClientFactory;

  @Scheduled(fixedDelayString = "${notification.scheduler.email.fixed-delay}")
  public void sendEmails() {
    List<Email> messages =
        emailRepository.findTop20ByStatusAndValidToIsNullOrderByScheduledAtAsc(
            MessageStatusEnum.SCHEDULED);

    if (messages != null && !messages.isEmpty()) {
      log.info(String.format("Sending '%d' emails", messages.size()));
      messages.parallelStream().filter(Objects::nonNull).forEach(emailClientFactory::send);
      log.info("sendEmails() task completed.");
    }
  }

}
