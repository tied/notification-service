package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMAIL_NOT_READY;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class EmailNotReadyException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -8095327021569740865L;

  public EmailNotReadyException(final String id) {
    this(null, id);
  }

  public EmailNotReadyException(final URI type, final String id) {
    super(
        type,
        EMAIL_NOT_READY.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The email [%s] is not ready to be sent.",
            EMAIL_NOT_READY.getCode(), id));
  }
}
