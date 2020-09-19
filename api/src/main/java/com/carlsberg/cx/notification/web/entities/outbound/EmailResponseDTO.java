package com.carlsberg.cx.notification.web.entities.outbound;

import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents a response for Email operations */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponseDTO {

  /** The Email ID */
  private String eid;

  /** The sender's application properties */
  private SenderResponseDTO from;

  /** The recipients details */
  private List<RecipientResponseDTO> to;

  /** The cc details */
  private List<RecipientResponseDTO> cc;

  /** The bcc details */
  private List<RecipientResponseDTO> bcc;

  /** The subject of the email */
  private String subject;

  /** The content of the email */
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

  /** The attachments meta info of the email */
  private List<EmailAttachmentDTO> attachments = new ArrayList<>();
}
