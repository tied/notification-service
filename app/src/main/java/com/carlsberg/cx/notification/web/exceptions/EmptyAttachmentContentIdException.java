package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum.INLINE;
import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.EMPTY_EMAIL_ATTACHMENT_CONTENT_ID;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class EmptyAttachmentContentIdException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -3630249181918681171L;

  public EmptyAttachmentContentIdException() {
    this(null);
  }

  public EmptyAttachmentContentIdException(final URI type) {
    super(
        type,
        EMPTY_EMAIL_ATTACHMENT_CONTENT_ID.getName(),
        BAD_REQUEST,
        String.format(
            "%s - If the attachment type is %s, then the content id cannot be empty!",
            EMPTY_EMAIL_ATTACHMENT_CONTENT_ID.getCode(), INLINE.toString()));
  }
}
