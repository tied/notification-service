package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.DUPLICATE_EMAIL_ADDRESS;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import java.util.Set;
import org.zalando.problem.AbstractThrowableProblem;

public class DuplicateEmailAddressException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -1136390809731778533L;

  public DuplicateEmailAddressException(final Set<String> emailAddresses) {
    this(null, emailAddresses);
  }

  public DuplicateEmailAddressException(final URI type, final Set<String> emailAddresses) {
    super(
        type,
        DUPLICATE_EMAIL_ADDRESS.getName(),
        BAD_REQUEST,
        String.format(
            "%s - Cannot use the same email address for the 'To', 'Cc' and 'Bcc' fields. "
                + "The duplicate email addresses found were: %s",
            DUPLICATE_EMAIL_ADDRESS.getCode(),
            String.join(", ", emailAddresses)));
  }
}
