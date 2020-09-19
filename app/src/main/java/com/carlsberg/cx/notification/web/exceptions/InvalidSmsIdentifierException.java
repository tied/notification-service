package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.INVALID_SMS_ID;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class InvalidSmsIdentifierException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -6763403315370800213L;

  public InvalidSmsIdentifierException(final String smsId) {
    this(null, smsId);
  }

  public InvalidSmsIdentifierException(final URI type, final String smsId) {
    super(
        type,
        INVALID_SMS_ID.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The supplied sms identifier [%s] does not match with any valid sms.",
            INVALID_SMS_ID.getCode(), smsId));
  }
}
