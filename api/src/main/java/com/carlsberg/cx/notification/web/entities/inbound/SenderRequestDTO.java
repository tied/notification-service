package com.carlsberg.cx.notification.web.entities.inbound;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents the sender's information details */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SenderRequestDTO {

  /** The sender's application code */
  @NotBlank
  private String applicationName;

  /** The sender's sub code */
  @NotBlank
  private String subCode;

  /** The sender's sales organization */
  @NotBlank
  private String salesOrg;
}
