package com.carlsberg.cx.notification.web.entities.outbound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** The DTO that represents a file attached to the email */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachmentDTO {

  /** Attachment ID */
  private String aid;

  /** The name of the file of this attachment */
  private String filename;

  /** The type of the file of this attachment */
  private String type;

  /** The disposition of this property in an email */
  private String disposition;

  /** The abbreviated encoded attachment content */
  private String data;

}
