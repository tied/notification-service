package com.carlsberg.cx.notification.web.entities.inbound;

import com.carlsberg.cx.notification.web.validations.PhoneNumber;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents a request for Sms operations */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestDTO {

  /** The Sms ID */
  @NotBlank
  private String sid;

  /** The sender's application properties */
  @NotNull
  @Valid
  private SenderRequestDTO from;

  /** The recipient phone number */
  @PhoneNumber
  private String to;

  /** The content of the sms */
  private String body;
}
