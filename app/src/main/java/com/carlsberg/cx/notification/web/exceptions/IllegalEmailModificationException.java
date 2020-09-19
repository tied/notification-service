package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.ILLEGAL_EMAIL_MODIFICATION;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class IllegalEmailModificationException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 3630730442234020298L;

  public IllegalEmailModificationException(final String id, final MessageStatusEnum status) {
    this(null, id, status);
  }

  public IllegalEmailModificationException(
      final URI type, final String id, final MessageStatusEnum status) {
    super(
        type,
        ILLEGAL_EMAIL_MODIFICATION.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The email [%s] is already in [%s] status and cannot be modified.",
            ILLEGAL_EMAIL_MODIFICATION.getCode(), id, status.toString()));
  }
}
