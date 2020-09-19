package com.carlsberg.cx.notification.web.exceptions;

import com.mongodb.MongoException;
import java.net.URI;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Log4j2
@ControllerAdvice
public class ExceptionHandling implements ProblemHandling {

  @ExceptionHandler(MongoException.class)
  public ResponseEntity<Problem> handleMongoException(MongoException exception) {
    log.error("a MongoException has occurred: " + exception.getMessage());

    Problem problem =
        Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withTitle("Database Error")
            .withDetail("I/O error when communicating with the database")
            .build();

    return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
  }

  /**
   * This is a special case where we override a Zalando Exception Handler (AdviceTrait) that is
   * overriding a Spring Exception Handler. See ProblemHandling -> IOAdviceTrait ->
   * MessageNotReadableAdviceTrait.
   */
  @Override
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Problem> handleMessageNotReadableException(
      final HttpMessageNotReadableException exception, final NativeWebRequest request) {

    log.error("a Json format exception has occurred: " + exception.getMessage());

    Problem problem =
        Problem.builder()
            .withStatus(Status.BAD_REQUEST)
            .withTitle("Structure Violation")
            .withDetail("One or more fields are not formatted correctly.")
            .build();

    return ResponseEntity.status(problem.getStatus().getStatusCode()).body(problem);
  }
}
