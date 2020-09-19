package com.carlsberg.cx.notification.web.config;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.documents.EmailAttachment;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.EmailConfig;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.SmsConfig;
import com.carlsberg.cx.notification.data.documents.Recipient;
import com.carlsberg.cx.notification.data.documents.Sender;
import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.services.config.MailTrapProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum;
import com.carlsberg.cx.notification.web.entities.inbound.AppMessageConfigurationDTO;
import com.carlsberg.cx.notification.web.entities.inbound.RecipientRequestDTO;
import com.carlsberg.cx.notification.web.entities.inbound.SenderRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.EmailAttachmentDTO;
import com.carlsberg.cx.notification.web.entities.outbound.EmailResponseDTO;
import com.carlsberg.cx.notification.web.entities.outbound.SmsResponseDTO;
import com.sendgrid.Attachments;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Mapper
public interface WebDataMapper {

  default EmailAttachment toEmailAttachment(
      String contentId, AttachmentTypeEnum attachmentType, MultipartFile file) throws IOException {

    EmailAttachment emailAttachment = new EmailAttachment();
    emailAttachment.setData(toContent(file.getBytes()));
    emailAttachment.setType(file.getContentType());
    emailAttachment.setFilename(file.getOriginalFilename());
    emailAttachment.setDisposition(attachmentType.toString().toLowerCase());

    if (AttachmentTypeEnum.ATTACHMENT.equals(attachmentType) && StringUtils.isEmpty(contentId)) {
      emailAttachment.setAid(UUID.randomUUID().toString());
    } else {
      emailAttachment.setAid(contentId);
    }

    return emailAttachment;
  }

  @Mapping(target = "contentId", source = "aid")
  @Mapping(target = "content", source = "data")
  public Attachments toAttachments(EmailAttachment emailAttachment);

  public SmsResponseDTO toSmsResponseDTO(Sms sms);

  public EmailResponseDTO toEmailResponseDTO(Email email);

  public Sender toSender(SenderRequestDTO senderRequest);

  public Recipient toRecipient(RecipientRequestDTO recipientRequests);

  public List<Recipient> toRecipients(List<RecipientRequestDTO> recipientRequests);

  // omits encoded attachment content
  @Mapping(
      target = "data",
      expression = "java( emailAttachment.getData() != null ? \"<content ommited>\" : null )")
  public EmailAttachmentDTO toEmailAttachmentDTO(EmailAttachment emailAttachment);

  // Converts byte[] email attachment content to encoded String
  default String toContent(byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }

  default JavaMailSender toJavaMailSender(MailTrapProperties mailTrapProperties) {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost("smtp.mailtrap.io");
    mailSender.setPort(25);
    mailSender.setUsername(mailTrapProperties.getMailtrapUsername());
    mailSender.setPassword(mailTrapProperties.getMailtrapPassword());

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "false");
    props.put("mail.smtp.starttls.enable", "false");

    return mailSender;
  }

  EmailConfig toConfig(EmailApplicationSubCodeSalesOrgProperties properties);

  SmsConfig toConfig(SmsApplicationSubCodeSalesOrgProperties properties);

  public AppMessageConfigurationDTO toAppMessageConfigurationDTO(
      MessageConfiguration messageConfiguration);

  public MessageConfiguration toMessageConfiguration(
      AppMessageConfigurationDTO appMessageConfigurationDTO);

  public List<AppMessageConfigurationDTO> toAppMessageConfigurationDTOList(
      List<MessageConfiguration> messageConfiguration);


}
