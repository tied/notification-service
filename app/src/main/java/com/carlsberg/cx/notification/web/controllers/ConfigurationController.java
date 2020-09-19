package com.carlsberg.cx.notification.web.controllers;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration;
import com.carlsberg.cx.notification.services.config.MessageConfigurationService;
import com.carlsberg.cx.notification.web.EndpointConstants;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import com.carlsberg.cx.notification.web.entities.inbound.AppMessageConfigurationDTO;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstants.PATH_CONFIG_SERVICES)
public class ConfigurationController {

  private final MessageConfigurationService messageConfigurationService;

  private final WebDataMapper webDataMapper;

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<AppMessageConfigurationDTO> getAllConfigurations() {

    final List<MessageConfiguration> allConfigurations =
        messageConfigurationService.getAllConfigurations();

    return webDataMapper.toAppMessageConfigurationDTOList(allConfigurations);
  }

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<AppMessageConfigurationDTO> updateApplicationConfiguration(
      @Valid @NotNull @RequestBody final AppMessageConfigurationDTO newAppConfig) {

    final MessageConfiguration messageConfiguration =
        webDataMapper.toMessageConfiguration(newAppConfig);

    messageConfigurationService.updateByApplication(messageConfiguration);

    final List<MessageConfiguration> allConfigurations =
        messageConfigurationService.getAllConfigurations();

    return webDataMapper.toAppMessageConfigurationDTOList(allConfigurations);

  }

}
