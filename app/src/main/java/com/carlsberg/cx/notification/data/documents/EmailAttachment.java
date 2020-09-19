package com.carlsberg.cx.notification.data.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents a file attached to the email */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachment {

  /** Attachment ID */
  private String aid;

  /** The name of the file of this attachment */
  private String filename;

  /** The type of the file of this attachment */
  private String type;

  /** The disposition of this property in an email */
  private String disposition;

  /** The encoded attachment content */
  private String data;
}
