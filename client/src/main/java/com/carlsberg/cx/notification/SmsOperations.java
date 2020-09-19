package com.carlsberg.cx.notification;

import static com.carlsberg.cx.notification.web.EndpointConstants.PATH_SMS_SERVICES;

import com.carlsberg.cx.notification.web.entities.inbound.SenderRequestDTO;
import com.carlsberg.cx.notification.web.entities.inbound.SmsRequestDTO;
import com.carlsberg.cx.notification.web.entities.outbound.SmsResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class SmsOperations {

  private final RestTemplate restTemplate;

  public SmsOperations(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public SmsResponseDTO create() {
    try {
      ResponseEntity<SmsResponseDTO> response =
          this.restTemplate.postForEntity(
              PATH_SMS_SERVICES, null, SmsResponseDTO.class);

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

  public SmsResponseDTO update(String sid, String applicationName, String subcode, String salesOrg, String to, String body) {
    try {
      SmsRequestDTO requestDTO = new SmsRequestDTO();
      requestDTO.setSid(sid);
      requestDTO.setFrom(new SenderRequestDTO(applicationName, subcode, salesOrg));
      requestDTO.setTo(to);
      requestDTO.setBody(body);

      ResponseEntity<SmsResponseDTO> response =
          this.restTemplate.exchange(
              PATH_SMS_SERVICES + "/" + sid,
              HttpMethod.PUT,
              new HttpEntity<>(requestDTO),
              SmsResponseDTO.class);

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

  public void cancel(String sid) {
    try {
      this.restTemplate.delete(PATH_SMS_SERVICES + "/" + sid);
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

  public void send(String sid) {
    try {
      this.restTemplate.postForEntity(PATH_SMS_SERVICES + "/" + sid,
          null, null);
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

  public SmsResponseDTO retrieve(String sid) {
    try {
      ResponseEntity<SmsResponseDTO> response =
          this.restTemplate.getForEntity(
              PATH_SMS_SERVICES + "/" + sid, SmsResponseDTO.class);

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
