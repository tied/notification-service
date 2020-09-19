package com.carlsberg.cx.notification.data.documents;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** An entity responsible for holding the email address and the name of a recipient. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipient {

  /** The name of a Recipient */
  private String name;

  /** The email address of a recipient */
  @NotBlank private String emailAddress;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Recipient recipient = (Recipient) o;
    return emailAddress.equals(recipient.emailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(emailAddress);
  }
}
