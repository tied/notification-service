package com.carlsberg.cx.notification.web.entities.outbound;

import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents a response for Sms operations */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponseDTO {

  /** The Sms ID */
  private String sid;

  /** The sender's application properties */
  private SenderResponseDTO from;

  /** The recipient phone number */
  private String to;

  /** The content of the sms */
  private String body;

  /** Status of the message */
  private MessageStatusEnum status;

  /** Date this message was sent at */
  private Date sentAt;

  // /** Date this message was created at */
  // private Date createdAt;
  //
  // /** Date this message was scheduled at */
  // private Date scheduledAt;
}
