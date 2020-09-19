package com.carlsberg.cx.notification.data.documents;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The entity that represents the sender's information details */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sender {

  /** The sender's application code */
  @NotBlank private String applicationName;

  /** The sender's sub code */
  @NotBlank private String subCode;

  /** The sender's sales organization */
  @NotBlank private String salesOrg;
}
