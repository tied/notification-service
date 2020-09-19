package com.carlsberg.cx.notification.services.sms;

import com.carlsberg.cx.notification.data.documents.Sender;
import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.data.repositories.SmsRepository;
import com.carlsberg.cx.notification.services.config.MessageConfigurationService;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import com.carlsberg.cx.notification.web.entities.inbound.SmsRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.SmsResponseDTO;
import com.carlsberg.cx.notification.web.exceptions.IllegalSmsModificationException;
import com.carlsberg.cx.notification.web.exceptions.InvalidSmsIdentifierException;
import com.carlsberg.cx.notification.web.exceptions.MissingApplicationConfigurationException;
import com.carlsberg.cx.notification.web.exceptions.SmsIdentifierMismatchException;
import com.carlsberg.cx.notification.web.exceptions.SmsNotReadyException;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class SmsService {

  private final SmsRepository smsRepository;
  private final WebDataMapper webDataMapper;
  private final MessageConfigurationService messageConfigurationService;

  public SmsResponseDTO create() {
    Sms sms =
        smsRepository.save(
            Sms.builder()
                .status(MessageStatusEnum.CREATED)
                .createdAt(new Date())
                .build());

    log.info("Created sms: " + sms.getSid());
    return webDataMapper.toSmsResponseDTO(sms);
  }

  public SmsResponseDTO update(
      final String smsId,
      final SmsRequestDTO dto)
      throws InvalidSmsIdentifierException {

    // sms id mismatch
    if (!smsId.equalsIgnoreCase(dto.getSid())) {
      throw new SmsIdentifierMismatchException(smsId, dto.getSid());
    }

    // find sms
    Sms sms = smsRepository.findBySidAndValidToIsNull(smsId);

    // if no sms exist for id
    if (sms == null) {
      throw new InvalidSmsIdentifierException(smsId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateSmsStatusForModification(sms.getSid(), sms.getStatus());

    // update fields
    sms.setFrom(webDataMapper.toSender(dto.getFrom()));
    sms.setTo(dto.getTo());
    sms.setBody(dto.getBody());

    if (sms.getFrom() != null) {
      // validate if configs exist for this app
      validateAppConfiguration(sms.getFrom());
    }

    // validate if mandatory fields are filled
    if (sms.getFrom() != null &&
        !StringUtils.isEmpty(sms.getTo()) &&
        !StringUtils.isEmpty(sms.getBody())) {

      sms.setStatus(MessageStatusEnum.READY);
    } else {
      sms.setStatus(MessageStatusEnum.CREATED);
    }

    // save entity
    sms = smsRepository.save(sms);

    log.info("Updated Sms: " + sms.getSid());

    // convert to dto
    return webDataMapper.toSmsResponseDTO(sms);
  }

  public void send(final String smsId) {

    // find sms
    Sms sms = smsRepository.findBySidAndValidToIsNull(smsId);

    // if no sms exist for id
    if (sms == null) {
      throw new InvalidSmsIdentifierException(smsId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateSmsStatusForModification(sms.getSid(), sms.getStatus());

    if (!MessageStatusEnum.READY.equals(sms.getStatus())) {
      throw new SmsNotReadyException(sms.getSid());
    }

    // To avoid NPN in certain cases
    if (sms.getFrom() == null) {
      throw new SmsNotReadyException(sms.getSid());
    }

    // validate if configs exist for this app
    validateAppConfiguration(sms.getFrom());

    // update status
    sms.setStatus(MessageStatusEnum.SCHEDULED);
    sms.setScheduledAt(new Date());

    // save entity
    sms = smsRepository.save(sms);
    log.info("Saved Sms: " + sms.getSid());
  }

  public void cancel(final String smsId) {

    // find sms
    Sms sms = smsRepository.findBySidAndValidToIsNull(smsId);

    // if no sms exist for id
    if (sms == null) {
      throw new InvalidSmsIdentifierException(smsId);
    }

    // throws exception if this message is in a state where modification is not allowed
    validateSmsStatusForModification(sms.getSid(), sms.getStatus());

    // update status
    sms.setStatus(MessageStatusEnum.CANCELED);

    // save entity
    sms = smsRepository.save(sms);

    log.info("Canceled Sms: " + sms.getSid());
  }

  public SmsResponseDTO retrieve(final String smsId) {

    Sms sms = smsRepository.findBySidAndValidToIsNull(smsId);

    // if no sms exist for id
    if (sms == null) {
      throw new InvalidSmsIdentifierException(smsId);
    }

    System.out.println("Found sms: " + sms.getSid());

    return webDataMapper.toSmsResponseDTO(sms);
  }

  /**
   * Validates if the message state allows for modifications.
   * 
   * @throws IllegalSmsModificationException if this message is in a state where modification is not
   *         allowed
   * @param sid message id
   * @param messageStatus message status
   */
  private void validateSmsStatusForModification(
      String sid, MessageStatusEnum messageStatus)
      throws IllegalSmsModificationException {

    if (MessageStatusEnum.SCHEDULED.equals(messageStatus) ||
        MessageStatusEnum.SENT.equals(messageStatus) ||
        MessageStatusEnum.CANCELED.equals(messageStatus)) {
      throw new IllegalSmsModificationException(sid, messageStatus);
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
    if (messageConfigurationService.fetchSmsConfig(
        from.getApplicationName(), from.getSubCode(), from.getSalesOrg()) == null) {
      throw new MissingApplicationConfigurationException(
          "sms", from.getApplicationName(), from.getSubCode(), from.getSalesOrg());
    }
  }
}
