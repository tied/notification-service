package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.SMS_IDENTIFIER_MISMATCH;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class SmsIdentifierMismatchException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 328497477969686463L;

  public SmsIdentifierMismatchException(final String paramSmsId, final String dtoSmsId) {
    this(null, paramSmsId, dtoSmsId);
  }

  public SmsIdentifierMismatchException(
      final URI type, final String paramSmsId, final String dtoSmsId) {
    super(
        type,
        SMS_IDENTIFIER_MISMATCH.getName(),
        BAD_REQUEST,
        String.format(
            "%s - Please confirm the sms identifier in the URL [%s] matches with the request body [%s].",
            SMS_IDENTIFIER_MISMATCH.getCode(), paramSmsId, dtoSmsId));
  }
}
