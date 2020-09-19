package com.carlsberg.cx.notification.services.email;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.documents.EmailAttachment;
import com.carlsberg.cx.notification.data.documents.Recipient;
import com.carlsberg.cx.notification.data.documents.Sender;
import com.carlsberg.cx.notification.data.repositories.EmailRepository;
import com.carlsberg.cx.notification.services.config.MessageConfigurationService;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.entities.inbound.EmailRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.EmailResponseDTO;
import com.carlsberg.cx.notification.web.exceptions.DuplicateEmailAddressException;
import com.carlsberg.cx.notification.web.exceptions.DuplicateEmailAttachmentContentIdException;
import com.carlsberg.cx.notification.web.exceptions.EmailIdentifierMismatchException;
import com.carlsberg.cx.notification.web.exceptions.EmailNotReadyException;
import com.carlsberg.cx.notification.web.exceptions.EmptyAttachmentContentIdException;
import com.carlsberg.cx.notification.web.exceptions.EmptyEmailAttachmentsException;
import com.carlsberg.cx.notification.web.exceptions.IllegalEmailModificationException;
import com.carlsberg.cx.notification.web.exceptions.InvalidEmailAttachmentIdentifierException;
import com.carlsberg.cx.notification.web.exceptions.InvalidEmailIdentifierException;
import com.carlsberg.cx.notification.web.exceptions.MissingApplicationConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

  private final EmailRepository emailRepository;
  private final WebDataMapper webDataMapper;
  private final MessageConfigurationService messageConfigurationService;

  public EmailResponseDTO create() {
    Email email =
        emailRepository.save(
            Email.builder()
                .status(MessageStatusEnum.CREATED)
                .createdAt(new Date())
                .build());

    log.info("Created email: " + email.getEid());
    return webDataMapper.toEmailResponseDTO(email);
  }

  public EmailResponseDTO update(
      final String emailId,
      final EmailRequestDTO dto)
      throws InvalidEmailIdentifierException {

    // email id mismatch
    if (!emailId.equalsIgnoreCase(dto.getEid())) {
      throw new EmailIdentifierMismatchException(emailId, dto.getEid());
    }

    // find email
    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateEmailStatusForModification(email.getEid(), email.getStatus());

    // update fields
    email.setSubject(dto.getSubject());
    email.setFrom(webDataMapper.toSender(dto.getFrom()));
    email.setTo(webDataMapper.toRecipients(dto.getTo()));
    email.setCc(webDataMapper.toRecipients(dto.getCc()));
    email.setBcc(webDataMapper.toRecipients(dto.getBcc()));

    // validate duplicate email addresses
    validateDuplicateEmailAddresses(email);

    email.setBody(dto.getBody());

    if (email.getFrom() != null) {
      // validate if configs exist for this app
      validateAppConfiguration(email.getFrom());
    }

    // validate if mandatory fields are filled
    if (email.getFrom() != null
        && email.getTo() != null
        && !email.getTo().isEmpty()
        && !StringUtils.isEmpty(email.getBody())
        && !StringUtils.isEmpty(email.getSubject())) {

      email.setStatus(MessageStatusEnum.READY);
    } else {
      email.setStatus(MessageStatusEnum.CREATED);
    }

    // save entity
    email = emailRepository.save(email);

    log.info("Updated Email: " + email.getEid());

    // convert to dto
    return webDataMapper.toEmailResponseDTO(email);
  }

  public void send(final String emailId) {

    // find email
    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateEmailStatusForModification(email.getEid(), email.getStatus());

    // if email is not ready
    if (!MessageStatusEnum.READY.equals(email.getStatus())) {
      throw new EmailNotReadyException(email.getEid());
    }

    // To avoid NPN in certain cases
    if (email.getFrom() == null) {
      throw new EmailNotReadyException(email.getEid());
    }

    // validate if configs exist for this app
    validateAppConfiguration(email.getFrom());

    // update status
    email.setStatus(MessageStatusEnum.SCHEDULED);
    email.setScheduledAt(new Date());

    // save entity
    emailRepository.save(email);
    log.info("Saved email: " + emailId);
  }

  public void cancel(final String emailId) {

    // find email
    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    // throws exception if this message is in a state where modification is not allowed
    if (MessageStatusEnum.SENT.equals(email.getStatus())
        || MessageStatusEnum.CANCELED.equals(email.getStatus())) {
      throw new IllegalEmailModificationException(email.getEid(), email.getStatus());
    }

    // allows canceling scheduled emails!

    // update status
    email.setStatus(MessageStatusEnum.CANCELED);

    // save entity
    email = emailRepository.save(email);
    log.info("Canceled Email: " + email.getEid());
  }

  public EmailResponseDTO addAttachment(
      final String emailId,
      final String contentId,
      final AttachmentTypeEnum attachmentType,
      final MultipartFile file)
      throws IOException {

    // find email
    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateEmailStatusForModification(email.getEid(), email.getStatus());

    if (AttachmentTypeEnum.INLINE.equals(attachmentType) && StringUtils.isEmpty(contentId)) {
      throw new EmptyAttachmentContentIdException();
    }

    if (file != null) {

      // convert multipart to EmailAttachment
      EmailAttachment attachment = webDataMapper.toEmailAttachment(contentId, attachmentType, file);

      boolean attachmentAlreadyExists =
          email.getAttachments().stream()
              .filter(Objects::nonNull)
              .map(EmailAttachment::getAid)
              .filter(Objects::nonNull)
              .anyMatch(contentId::equalsIgnoreCase);

      if (attachmentAlreadyExists) {
        throw new DuplicateEmailAttachmentContentIdException(contentId);
      }

      if (attachment != null) {
        // save email
        email.getAttachments().add(attachment);
        email = emailRepository.save(email);
        log.info("Saved email: " + emailId);
      }
    }

    // convert Email to EmailResponseDTO
    return webDataMapper.toEmailResponseDTO(email);
  }

  public void deleteAttachment(
      final String emailId,
      final String attachmentId) {

    // find email
    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateEmailStatusForModification(email.getEid(), email.getStatus());

    if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {

      // check if attachment exists
      EmailAttachment attachment =
          email.getAttachments().stream()
              .filter(attach -> attachmentId.equalsIgnoreCase(attach.getAid()))
              .findFirst()
              .orElse(null);

      // delete and save if it exists
      if (attachment != null) {
        log.info("Deleting attachment: " + attachment.getFilename());
        email.getAttachments().remove(attachment);

        emailRepository.save(email);
        log.info("Saved email: " + emailId);

      } else {
        // email attachment not found
        throw new InvalidEmailAttachmentIdentifierException(emailId, attachmentId);
      }

    } else {
      // no attachments to delete
      throw new EmptyEmailAttachmentsException(emailId);
    }
  }

  public void deleteAttachments(final String emailId) {

    // find email
    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateEmailStatusForModification(email.getEid(), email.getStatus());

    if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {
      log.info("Deleting attachments for email: " + emailId);
      email.getAttachments().clear();

      emailRepository.save(email);
      log.info("Saved email: " + emailId);
    } else {
      throw new EmptyEmailAttachmentsException(emailId);
    }
  }

  public EmailResponseDTO retrieve(final String emailId) {

    Email email = emailRepository.findByEidAndValidToIsNull(emailId);

    // if no email exist for id
    if (email == null) {
      throw new InvalidEmailIdentifierException(emailId);
    }

    log.info("Found email: " + email.getEid());

    return webDataMapper.toEmailResponseDTO(email);
  }

  /**
   * Validates if the message state allows for modifications.
   *
   * @throws IllegalEmailModificationException if this message is in a state where modification is
   *         not allowed
   * @param eid message id
   * @param messageStatus message status
   */
  private void validateEmailStatusForModification(
      final String eid,
      final MessageStatusEnum messageStatus)
      throws IllegalEmailModificationException {

    if (MessageStatusEnum.SCHEDULED.equals(messageStatus)
        || MessageStatusEnum.SENT.equals(messageStatus)
        || MessageStatusEnum.CANCELED.equals(messageStatus)) {
      throw new IllegalEmailModificationException(eid, messageStatus);
    }
  }

  /**
   * Validates if there are duplicate email addresses in this email.
   *
   * @throws DuplicateEmailAddressException if this message contains the same email address for one
   *         or more of the receiver fields.
   * @param email email message to validate
   */
  private void validateDuplicateEmailAddresses(
      final Email email)
      throws DuplicateEmailAddressException {

    Set<String> duplicates = new TreeSet<>();
    List<Recipient> recipients = new ArrayList<>();

    recipients.addAll(email.getTo());
    recipients.addAll(email.getCc());
    recipients.addAll(email.getBcc());

    recipients.stream()
        .filter(i -> Collections.frequency(recipients, i) > 1)
        .map(Recipient::getEmailAddress)
        .forEach(duplicates::add);

    if (!duplicates.isEmpty()) {
      throw new DuplicateEmailAddressException(duplicates);
    }
  }

  /**
   * Validates if there is a configuration for this application defined in the database.
   * 
   * @param from The sender information with application, subcode and salesOrg data to validate.
   * @throws MissingApplicationConfigurationException if no configuration was found for this
   *         application.
   */
  public void validateAppConfiguration(Sender from)
      throws MissingApplicationConfigurationException {
    if (messageConfigurationService.fetchEmailConfig(
        from.getApplicationName(), from.getSubCode(), from.getSalesOrg()) == null) {
      throw new MissingApplicationConfigurationException(
          "email", from.getApplicationName(), from.getSubCode(), from.getSalesOrg());
    }
  }
}
