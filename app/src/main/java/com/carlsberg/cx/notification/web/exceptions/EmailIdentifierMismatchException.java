package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMAIL_IDENTIFIER_MISMATCH;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class EmailIdentifierMismatchException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 1060593969817399464L;

  public EmailIdentifierMismatchException(final String paramEmailId, final String dtoEmailId) {
    this(null, paramEmailId, dtoEmailId);
  }

  public EmailIdentifierMismatchException(
      final URI type, final String paramEmailId, final String dtoEmailId) {
    super(
        type,
        EMAIL_IDENTIFIER_MISMATCH.getName(),
        BAD_REQUEST,
        String.format(
            "%s - Please confirm the email identifier in the URL [%s] matches with the request body [%s].",
            EMAIL_IDENTIFIER_MISMATCH.getCode(), paramEmailId, dtoEmailId));
  }
}
