package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMAIL_NOT_SENT;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.carlsberg.cx.notification.data.documents.Email;
import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class EmailSendException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 137228532995925820L;

  public EmailSendException(final Email email) {
    this(null, email);
  }

  public EmailSendException(final URI uri, final Email email) {
    super(
        uri,
        EMAIL_NOT_SENT.getName(),
        BAD_REQUEST,
        String.format("%s - Unable to send email: %s.", EMAIL_NOT_SENT.getCode(), email.getEid()));
  }
}
