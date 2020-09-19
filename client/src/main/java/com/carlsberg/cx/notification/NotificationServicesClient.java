package com.carlsberg.cx.notification;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Getter
public class NotificationServicesClient {

  private final EmailOperations emailOperations;
  private final SmsOperations smsOperations;

  public NotificationServicesClient(
      final String baseUrl,
      final String clientUsername,
      final String clientPassword) {

    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    MappingJackson2HttpMessageConverter messageConverter =
        new MappingJackson2HttpMessageConverter();
    messageConverter.setPrettyPrint(false);
    messageConverter.setObjectMapper(objectMapper);

    final RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().removeIf(
        m -> m.getClass().getName().equals(MappingJackson2HttpMessageConverter.class.getName()));
    restTemplate.getMessageConverters().add(messageConverter);

    final DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory(baseUrl);
    builderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
    restTemplate.setUriTemplateHandler(builderFactory);

    restTemplate.getInterceptors()
        .add(new BasicAuthenticationInterceptor(clientUsername, clientPassword,
            StandardCharsets.UTF_8));

    this.emailOperations = new EmailOperations(restTemplate);
    this.smsOperations = new SmsOperations(restTemplate);
  }

}
