package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.SMS_NOT_READY;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class SmsNotReadyException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 28368646298397033L;

  public SmsNotReadyException(final String smsId) {
    this(null, smsId);
  }

  public SmsNotReadyException(final URI type, final String smsId) {
    super(
        type,
        SMS_NOT_READY.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The sms [%s] is not ready to be sent.", SMS_NOT_READY.getCode(), smsId));
  }
}
