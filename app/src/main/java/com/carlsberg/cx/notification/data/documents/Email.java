package com.carlsberg.cx.notification.data.documents;

import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Email {

  /** The Email ID */
  @Id private String eid;

  /** The sender's application properties */
  private Sender from;

  /** The recipients details */
  private List<Recipient> to;

  /** The cc details */
  private List<Recipient> cc;

  /** The bcc details */
  private List<Recipient> bcc;

  /** The subject of the email */
  private String subject;

  /** The content of the email */
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

  /** The attachments meta info of the email */
  @Builder.Default private List<EmailAttachment> attachments = new ArrayList<>();
}
