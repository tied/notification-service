package com.carlsberg.cx.notification.web.entities.inbound;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents the recipient's information details */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientRequestDTO {

  /** The recipient's name */
  private String name;

  /** The recipient's email address */
  @NotBlank
  @Email
  private String emailAddress;
}
