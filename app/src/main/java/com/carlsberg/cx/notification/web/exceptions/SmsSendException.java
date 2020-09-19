package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.SMS_NOT_SENT;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.carlsberg.cx.notification.data.documents.Sms;
import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class SmsSendException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -9027413174291459183L;

  public SmsSendException(final Sms sms) {
    this(null, sms);
  }

  public SmsSendException(final URI uri, final Sms sms) {
    super(
        uri,
        SMS_NOT_SENT.getName(),
        BAD_REQUEST,
        String.format("%s - Unable to send sms: %s.", SMS_NOT_SENT.getCode(), sms.getSid()));
  }
}
