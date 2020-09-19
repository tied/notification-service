package com.carlsberg.cx.notification;

import static com.carlsberg.cx.notification.web.EndpointConstants.PATH_ATTACHMENT;
import static com.carlsberg.cx.notification.web.EndpointConstants.PATH_EMAIL_SERVICES;

import com.carlsberg.cx.notification.web.entities.enums.AttachmentTypeEnum;
import com.carlsberg.cx.notification.web.entities.inbound.EmailRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.EmailResponseDTO;
import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class EmailOperations {

  private final RestTemplate restTemplate;

  public EmailOperations(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public EmailResponseDTO create() {
    try {
      ResponseEntity<EmailResponseDTO> response =
          this.restTemplate.postForEntity(
              PATH_EMAIL_SERVICES, null, EmailResponseDTO.class);

      return response.getBody();
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public EmailResponseDTO update(String eid, EmailRequestDTO dto) {
    try {
      ResponseEntity<EmailResponseDTO> response =
          this.restTemplate.exchange(
              PATH_EMAIL_SERVICES + "/" + eid,
              HttpMethod.PUT,
              new HttpEntity<>(dto),
              EmailResponseDTO.class);

      return response.getBody();
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public EmailResponseDTO addAttachment(String eid, File file, String contentId, AttachmentTypeEnum attachmentType) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", new FileSystemResource(file));
      body.add("contentId", contentId);
      body.add("attachmentType", attachmentType.toString());

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<EmailResponseDTO> response =
          this.restTemplate.postForEntity(
              PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT,
              requestEntity, EmailResponseDTO.class);

      return response.getBody();
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public void deleteAttachment(String eid, String aid) {
    try {
      this.restTemplate.delete(PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT + "/" + aid);
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public void deleteAttachments(String eid) {
    try {
      this.restTemplate.delete(PATH_EMAIL_SERVICES + "/" + eid + PATH_ATTACHMENT);
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public void cancel(String eid) {
    try {
      this.restTemplate.delete(PATH_EMAIL_SERVICES + "/" + eid);
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public void send(String eid) {
    try {
      this.restTemplate.postForEntity(
          PATH_EMAIL_SERVICES + "/" + eid, null, null);
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }

  public EmailResponseDTO retrieve(String eid) {
    try {
      ResponseEntity<EmailResponseDTO> response =
          this.restTemplate.getForEntity(
              PATH_EMAIL_SERVICES + "/" + eid, EmailResponseDTO.class);

      return response.getBody();
    } catch (HttpClientErrorException.BadRequest ex400) {
      throw new RuntimeException("Bad Request");
    } catch (HttpClientErrorException.Unauthorized ex401) {
      throw new RuntimeException("Invalid client authentication");
    } catch (HttpClientErrorException.Forbidden ex403) {
      throw new RuntimeException("Client not authorized to validate external user authentication");
    } catch (HttpClientErrorException.NotFound ex404) {
      throw new RuntimeException();
    }
  }
}
