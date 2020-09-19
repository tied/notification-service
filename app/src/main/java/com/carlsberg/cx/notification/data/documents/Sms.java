package com.carlsberg.cx.notification.data.documents;

import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Sms {

  /** The Sms ID */
  @Id private String sid;

  /** The sender's application properties */
  private Sender from;

  /** The recipient phone number */
  private String to;

  /** The content of the sms */
  private String body;

  /** Date after which this message is no longer valid */
  private Date validTo;

  /** Date this message was sent at */
  private Date sentAt;

  /** Date this message was created at */
  private Date createdAt;

  /** Date this message was scheduled at */
  private Date scheduledAt;

  /** Status of the message */
  private MessageStatusEnum status;

  /** The number of retried attempts */
  private int retryAttempts = 0;
}
