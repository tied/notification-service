package com.carlsberg.cx.notification.web.exceptions;

import static com.carlsberg.cx.notification.web.config.NotificationExceptionEnum.MISSING_APPLICATION_CONFIGURATION;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;

public class MissingApplicationConfigurationException extends AbstractThrowableProblem {

  private static final long serialVersionUID = -8366055545297746447L;

  public MissingApplicationConfigurationException(
      final String type, final String application, final String subcode, final String salesOrg) {
    this(null, type, application, subcode, salesOrg);
  }

  public MissingApplicationConfigurationException(
      final URI uri,
      final String type,
      final String application,
      final String subcode,
      final String salesOrg) {

    super(
        uri,
        MISSING_APPLICATION_CONFIGURATION.getName(),
        BAD_REQUEST,
        String.format(
            "%s - There is no %s configuration defined for this application. "
                + "Application: %s, Subcode: %s, SalesOrg: %s.",
            MISSING_APPLICATION_CONFIGURATION.getCode(), type, application, subcode, salesOrg));
  }
}
