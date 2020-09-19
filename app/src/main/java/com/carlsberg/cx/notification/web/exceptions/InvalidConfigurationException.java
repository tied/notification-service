package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.INVALID_CONFIGURATION;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import java.util.Collection;
import org.zalando.problem.AbstractThrowableProblem;

public class InvalidConfigurationException extends AbstractThrowableProblem {

  private static final long serialVersionUID = 6147283218092423305L;

  public InvalidConfigurationException(final String type, final Collection<String> errorMessages) {
    this(null, type, errorMessages);
  }

  public InvalidConfigurationException(
      final URI uri, final String type, final Collection<String> errorMessages) {
    super(
        uri,
        INVALID_CONFIGURATION.getName(),
        BAD_REQUEST,
        String.format(
            "%s - Found (%d) errors when validating the %s configuration: ",
            INVALID_CONFIGURATION.getCode(), errorMessages.size(), String.join("\n", errorMessages)));
  }
}
