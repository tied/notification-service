package com.carlsberg.cx.notification;

import static com.carlsberg.cx.notification.web.EndpointConstants.PATH_SMS_SERVICES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.web.entities.inbound.SenderRequestDTO;
import com.carlsberg.cx.notification.web.entities.inbound.SmsRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.SmsResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class SmsFunctionalTest extends BaseIntegrationTest {

  private String createSms(final String prefix)
      throws Exception,
      JsonProcessingException,
      JsonMappingException,
      UnsupportedEncodingException {

    MvcResult createResult =
        perform(
            post(PATH_SMS_SERVICES),
            "sms",
            prefix + "_prepare_create")
                .andReturn();

    SmsResponseDTO smsResponseDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            SmsResponseDTO.class);

    return smsResponseDto.getSid();
  }

  @Test
  @Tag("functional")
  void create() throws Exception {

    perform(
        post(PATH_SMS_SERVICES),
        "sms",
        "create");
  }

  @Test
  @Tag("functional")
  void update() throws Exception {

    String sid = createSms("update");

    SenderRequestDTO senderRequestDTO =
      SenderRequestDTO.builder()
        .applicationName("CSPLUS")
        .subCode("BILLING")
        .salesOrg("D0001")
      .build();

    SmsRequestDTO requestDTO =
      SmsRequestDTO.builder()
        .sid(sid)
        .from(senderRequestDTO)
        .to("+351917654321")
        .body("Test body content")
      .build();

    perform(
        put(PATH_SMS_SERVICES + "/" + requestDTO.getSid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)),
        "sms",
        "update");
  }

  @Test
  @Tag("functional")
  void cancel() throws Exception {

    String sid = createSms("cancel");

    perform(
        delete(PATH_SMS_SERVICES + "/" + sid),
        "sms",
        "cancel");
  }

  @Test
  @Tag("functional")
  void send() throws Exception {

    String sid = createSms("send");

    SenderRequestDTO senderRequestDTO =
        SenderRequestDTO.builder()
            .applicationName("CSPLUS")
            .subCode("BILLING")
            .salesOrg("D0001")
            .build();

    SmsRequestDTO requestDTO =
        SmsRequestDTO.builder()
            .sid(sid)
            .from(senderRequestDTO)
            .to("+351917654321")
            .body("Test body content")
            .build();

    perform(
        put(PATH_SMS_SERVICES + "/" + requestDTO.getSid())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO)),
        "sms",
        "send_prepare_01");

    doNothing().when(smsClientFactoryMock).send(any(Sms.class));

    perform(
        post(PATH_SMS_SERVICES + "/" + sid),
        "sms",
        "send");
  }

  @Test
  @Tag("functional")
  void retrieve() throws Exception {

    String sid = createSms("retrieve");

    perform(
        get(PATH_SMS_SERVICES + "/" + sid),
        "sms",
        "retrieve");
  }
}
