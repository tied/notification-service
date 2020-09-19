package com.carlsberg.cx.notification.web.controllers;

import com.carlsberg.cx.notification.services.sms.SmsService;
import com.carlsberg.cx.notification.web.EndpointConstants;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.inbound.SmsRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.SmsResponseDTO;
import com.carlsberg.cx.notification.web.exceptions.SmsIdentifierMismatchException;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.PATH_SMS_SERVICES)
public class SmsController {

  private final WebDataMapper webDataMapper;
  private final SmsService smsService;

  /**
   * Create a new draft of a SMS.
   * @return SmsResponseDTO with the ID of the sms that was just created.
   */
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public SmsResponseDTO create() {
    return smsService.create();
  }

  /**
   * Update the content of an unsent SMS.
   * @param smsId The ID of the SMS to update.
   * @param smsRequestDTO The DTO that represents a sms request with the data to update.
   * @return The DTO that represents a sms response with the sms's data after the update.
   */
  @PutMapping(
      value = "/{sid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public SmsResponseDTO update(
      @NotBlank @PathVariable("sid") String smsId,
      @Valid @NotNull @RequestBody SmsRequestDTO smsRequestDTO) {
    return smsService.update(smsId, smsRequestDTO);
  }

  /**
   * Cancel an unsent sms.
   * @param smsId The ID of the sms to cancel.
   */
  @DeleteMapping(value = "/{sid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@NotBlank @PathVariable("sid") String smsId) {
    smsService.cancel(smsId);
  }

  /**
   * Send an sms.
   * @param smsId The ID of the sms to send.
   */
  @PostMapping(value = "/{sid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void send(@NotBlank @PathVariable("sid") String smsId) {
    smsService.send(smsId);
  }

  /**
   * Retrieve an sms by id.
   * @param smsId The ID of the sms to retrieve.
   * @Return SmsResponseDTO with the details of the sms.
   */
  @GetMapping(value = "/{sid}")
  public SmsResponseDTO retrieve(@NotBlank @PathVariable("sid") String smsId) {
    return smsService.retrieve(smsId);
  }
}
