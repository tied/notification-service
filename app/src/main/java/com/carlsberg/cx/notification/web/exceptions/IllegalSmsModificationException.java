package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.ILLEGAL_SMS_MODIFICATION;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class IllegalSmsModificationException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 8977699544035439009L;

  public IllegalSmsModificationException(final String id, final MessageStatusEnum status) {
    this(null, id, status);
  }

  public IllegalSmsModificationException(
      final URI type, final String id, final MessageStatusEnum status) {
    super(
        type,
        ILLEGAL_SMS_MODIFICATION.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The sms [%s] is already in [%s] status and cannot be modified.",
            ILLEGAL_SMS_MODIFICATION.getCode(), id, status.toString()));
  }
}
