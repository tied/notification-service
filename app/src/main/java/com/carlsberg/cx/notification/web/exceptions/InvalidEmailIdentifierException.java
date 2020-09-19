package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.INVALID_EMAIL_ID;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class InvalidEmailIdentifierException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -6052023458129410964L;

  public InvalidEmailIdentifierException(final String id) {
    this(null, id);
  }

  public InvalidEmailIdentifierException(final URI type, final String id) {
    super(
        type,
        INVALID_EMAIL_ID.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The supplied email identifier [%s] does not match with any valid email.",
            INVALID_EMAIL_ID.getCode(), id));
  }
}
