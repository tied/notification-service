package com.carlsberg.cx.notification.web.entities.inbound;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** The DTO that represents a request for Email operations */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

  /** The Email ID */
  @NotBlank
  private String eid;

  /** The sender's application properties */
  @NotNull
  @Valid
  private SenderRequestDTO from;

  /** The recipients details */
  @NotEmpty
  @Valid
  private List<RecipientRequestDTO> to;

  /** The cc details */
  @NotNull
  @Valid
  private List<RecipientRequestDTO> cc;

  /** The bcc details */
  @NotNull
  @Valid
  private List<RecipientRequestDTO> bcc;

  /** The subject of the email */
  private String subject;

  /** The content of the email */
  private String body;
}
