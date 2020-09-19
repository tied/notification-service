package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.INVALID_EMAIL_ATTACHMENT_ID;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class InvalidEmailAttachmentIdentifierException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -4496331792972049900L;

  public InvalidEmailAttachmentIdentifierException(
      final String emailId, final String attachmentId) {
    this(null, emailId, attachmentId);
  }

  public InvalidEmailAttachmentIdentifierException(
      final URI type, final String emailId, final String attachmentId) {
    super(
        type,
        INVALID_EMAIL_ATTACHMENT_ID.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The supplied email attachment identifier [%s] does not exist for this email [%s].",
            INVALID_EMAIL_ATTACHMENT_ID.getCode(), attachmentId, emailId));
  }
}
