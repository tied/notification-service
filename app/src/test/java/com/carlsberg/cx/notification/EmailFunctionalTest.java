package com.carlsberg.cx.notification;

import static com.carlsberg.cx.notification.web.EndpointConstants.PATH_ATTACHMENT;
import static com.carlsberg.cx.notification.web.EndpointConstants.PATH_EMAIL_SERVICES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum;
import com.carlsberg.cx.notification.web.entities.inbound.EmailRequestDTO;
import com.carlsberg.cx.notification.web.entities.inbound.RecipientRequestDTO;
import com.carlsberg.cx.notification.web.entities.inbound.SenderRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.EmailResponseDTO;
import com.carlsberg.cx.notification.web.exceptions.EmailSendException;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;


public class EmailFunctionalTest extends BaseIntegrationTest {

  private String createEmail(final String prefix)
      throws Exception {

    MvcResult createResult =
        perform(
            post(PATH_EMAIL_SERVICES),
            "email",
            prefix + "_prepare_create")
                .andReturn();

    EmailResponseDTO emailResponseDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            EmailResponseDTO.class);

    return emailResponseDto.getEid();
  }

  private String addAttachment(final String prefix, final String eid) throws Exception {

    MvcResult createResult =
        perform(
            multipart(PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT)
                .file(new MockMultipartFile(
                    "file", "multipartTextFile.txt",
                    ContentType.DEFAULT_TEXT.toString(), "testing multipart file".getBytes()))
                .param("contentId", "12345")
                .param("attachmentType", AttachmentTypeEnum.ATTACHMENT.toString()),
            "email",
            prefix + "_prepare_attachment")
                .andReturn();

    EmailResponseDTO emailResponseDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            EmailResponseDTO.class);

    return emailResponseDto.getAttachments().get(0).getAid();
  }

  @Test
  @Tag("functional")
  void create() throws Exception {

    perform(
        post(PATH_EMAIL_SERVICES),
        "email",
        "create");
  }

  @Test
  @Tag("functional")
  void update() throws Exception {

    String eid = createEmail("update");

    SenderRequestDTO senderRequestDTO =
        SenderRequestDTO.builder()
            .applicationName("CSPLUS")
            .subCode("BILLING")
            .salesOrg("D0001")
            .build();

    RecipientRequestDTO recipient =
        RecipientRequestDTO.builder()
          .name("Recipient test name")
          .emailAddress("recipient@test.com")
        .build();

    RecipientRequestDTO cc =
        RecipientRequestDTO.builder()
            .name("Recipient cc name")
            .emailAddress("recipient-cc@test.com")
            .build();

    RecipientRequestDTO bcc =
        RecipientRequestDTO.builder()
            .name("Recipient bcc name")
            .emailAddress("recipient-bcc@test.com")
            .build();

    EmailRequestDTO requestDTO =
        EmailRequestDTO.builder()
            .eid(eid)
            .from(senderRequestDTO)
            .cc(List.of(cc))
            .bcc(List.of(bcc))
            .to(List.of(recipient))
            .subject("Test subject")
            .body("Test body content")
            .build();

    perform(
        put(PATH_EMAIL_SERVICES + "/" + requestDTO.getEid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)),
        "email",
        "update");
  }

  @Test
  @Tag("functional")
  void addAttachment() throws Exception {

    String eid = createEmail("addAttachment");

    perform(
        multipart(PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT)
            .file(new MockMultipartFile(
                "file", "multipartTextFile.txt",
                ContentType.DEFAULT_TEXT.toString(), "testing multipart file".getBytes()))
            .param("contentId", "12345")
            .param("attachmentType", AttachmentTypeEnum.ATTACHMENT.toString()),
        "email",
        "addAttachment");
  }

  @Test
  @Tag("functional")
  void deleteAttachment() throws Exception {

    String eid = createEmail("deleteAttachment");

    String aid = addAttachment("deleteAttachment", eid);

    perform(
        delete(PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT + "/" + aid),
        "email",
        "deleteAttachment");
  }

  @Test
  @Tag("functional")
  void deleteAttachments() throws Exception {

    String eid = createEmail("deleteAttachments");

    addAttachment("deleteAttachment", eid);

    perform(
        delete(PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT),
        "email",
        "deleteAttachments");
  }

  @Test
  @Tag("functional")
  void cancel() throws Exception {

    String eid = createEmail("cancel");

    perform(
        delete(PATH_EMAIL_SERVICES + "/" + eid),
        "email",
        "cancel");
  }

  @Test
  @Tag("functional")
  void send() throws Exception, EmailSendException {

    String eid = createEmail("send");

    SenderRequestDTO senderRequestDTO =
        SenderRequestDTO.builder()
            .applicationName("CSPLUS")
            .subCode("BILLING")
            .salesOrg("D0001")
            .build();

    RecipientRequestDTO recipient =
        RecipientRequestDTO.builder()
            .name("Recipient test name")
            .emailAddress("recipient@test.com")
            .build();

    RecipientRequestDTO cc =
        RecipientRequestDTO.builder()
            .name("Recipient cc name")
            .emailAddress("recipient-cc@test.com")
            .build();

    RecipientRequestDTO bcc =
        RecipientRequestDTO.builder()
            .name("Recipient bcc name")
            .emailAddress("recipient-bcc@test.com")
            .build();

    EmailRequestDTO requestDTO =
        EmailRequestDTO.builder()
            .eid(eid)
            .from(senderRequestDTO)
            .cc(List.of(cc))
            .bcc(List.of(bcc))
            .to(List.of(recipient))
            .subject("Test subject")
            .body("Test body content")
            .build();

    perform(
        put(PATH_EMAIL_SERVICES + "/" + requestDTO.getEid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)),
        "email",
        "send_prepare_update");

    doNothing()
        .when(emailClientFactoryMock)
        .send(any(Email.class));

    perform(
        post(PATH_EMAIL_SERVICES + "/" + eid),
        "email",
        "send");
  }

  @Test
  @Tag("functional")
  void retrieve() throws Exception {

    String eid = createEmail("retrieve");

    perform(
        get(PATH_EMAIL_SERVICES + "/" + eid),
        "email",
        "retrieve");
  }
}
