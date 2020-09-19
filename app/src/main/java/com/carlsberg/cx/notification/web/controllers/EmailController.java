package com.carlsberg.cx.notification.web.controllers;

import com.carlsberg.cx.notification.services.email.EmailService;
import com.carlsberg.cx.notification.web.EndpointConstants;
import com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum;
import com.carlsberg.cx.notification.web.entities.inbound.EmailRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.EmailResponseDTO;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.PATH_EMAIL_SERVICES)
public class EmailController {

  private final EmailService emailService;

  /**
   * Create a new draft email.
   * @return EmailResponseDTO with the ID of the email that was just created.
   */
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE)
  public EmailResponseDTO create() {
    return emailService.create();
  }

  /**
   * Update the content of an unsent email.
   * @param emailId The ID of the email to update.
   * @param emailRequestDTO The DTO that represents an email request with the data to update.
   * @return The DTO that represents an email response with the email's data after the update.
   */
  @PutMapping(
      value = "/{eid}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public EmailResponseDTO update(
      @NotBlank @PathVariable("eid") String emailId,
      @Valid @NotNull @RequestBody EmailRequestDTO emailRequestDTO) {

    return emailService.update(emailId, emailRequestDTO);
  }

  /**
   * Add an attachment to an email.
   * @param emailId The ID of the email
   * @param contentId The content ID of the attachment
   * @param attachmentType The type of the attachment (inline/attachment)
   * @param file The file to attach to the email
   * @return EmailResponseDTO The DTO that represents a response.
   * @throws IOException
   */
  @PostMapping(
      value = "/{eid}/attachment",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public EmailResponseDTO addAttachment(
      @NotBlank @PathVariable("eid") String emailId,
      @RequestParam("file") MultipartFile file,
      @RequestParam("contentId") String contentId,
      @NotNull @RequestParam("attachmentType") AttachmentTypeEnum attachmentType)
      throws IOException {

    return emailService.addAttachment(emailId, contentId, attachmentType, file);
  }

  /**
   * Delete an attachment from an email.
   *
   * @param emailId The ID of the email
   * @param attachmentId The ID of the attachment
   */
  @DeleteMapping(
      value = "/{eid}/attachment/{aid}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAttachment(
      @NotBlank @PathVariable("eid") String emailId,
      @NotBlank @PathVariable("aid") String attachmentId) {

    emailService.deleteAttachment(emailId, attachmentId);
  }

  /**
   * Deletes all attachments from an email.
   * @param emailId The ID of the email
   */
  @DeleteMapping(
      value = "/{eid}/attachment",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAttachments(
      @NotBlank @PathVariable("eid") String emailId) {

    emailService.deleteAttachments(emailId);
  }

  /**
   * Cancel an unsent email.
   * @param emailId The ID of the email to cancel.
   */
  @DeleteMapping(value = "/{eid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(
      @NotBlank @PathVariable("eid") String emailId){

    emailService.cancel(emailId);
  }

  /**
   * Send an email.
   * @param emailId The ID of the email to send.
   */
  @PostMapping(value = "/{eid}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void send(
      @NotBlank @PathVariable("eid") String emailId){

    emailService.send(emailId);
  }

  /**
   * Retrieve an Email by id.
   * @param emailId The ID of the email to retrieve.
   * @return EmailResponseDTO with the details of the email
   */
  @GetMapping(value = "{eid}")
  public EmailResponseDTO retrieve(
      @NotBlank @PathVariable("eid") String emailId) {

    return emailService.retrieve(emailId);
  }
}
