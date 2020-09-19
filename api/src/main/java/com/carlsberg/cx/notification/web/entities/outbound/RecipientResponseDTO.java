package com.carlsberg.cx.notification.web.entities.outbound;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents the recipient's information details */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientResponseDTO {

  /** The recipient's name */
  private String name;

  /** The recipient's email address */
  @NotBlank private String emailAddress;
}
