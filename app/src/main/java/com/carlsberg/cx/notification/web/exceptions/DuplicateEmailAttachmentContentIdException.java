package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.DUPLICATE_EMAIL_ATTACHMENT_CONTENT_ID;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class DuplicateEmailAttachmentContentIdException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -1942631520672420869L;

  public DuplicateEmailAttachmentContentIdException(final String contentId) {
    this(null, contentId);
  }

  public DuplicateEmailAttachmentContentIdException(final URI type, final String contentId) {
    super(
        type,
        DUPLICATE_EMAIL_ATTACHMENT_CONTENT_ID.getName(),
        BAD_REQUEST,
        String.format(
            "%s - The content id [%s] for this attachment already exists.",
            DUPLICATE_EMAIL_ATTACHMENT_CONTENT_ID.getCode(), contentId));
  }
}
