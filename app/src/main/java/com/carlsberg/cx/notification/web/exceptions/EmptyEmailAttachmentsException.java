package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMPTY_EMAIL_ATTACHMENTS;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class EmptyEmailAttachmentsException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 3839108776233231050L;

  public EmptyEmailAttachmentsException(final String id) {
    this(null, id);
  }

  public EmptyEmailAttachmentsException(final URI type, final String id) {
    super(
        type,
        EMPTY_EMAIL_ATTACHMENTS.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The selected email [%s] does not have any attachments to be deleted.",
            EMPTY_EMAIL_ATTACHMENTS.getCode(), id));
  }
}
