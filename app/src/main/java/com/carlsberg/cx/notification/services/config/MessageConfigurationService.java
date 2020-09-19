package com.carlsberg.cx.notification.services.config;

import static com.carlsberg.cx.notification.services.config.ApplicationConstants.DEFAULT_KEYWORD;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.EmailConfig;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.SmsConfig;
import com.carlsberg.cx.notification.data.repositories.MessageConfigurationRepository;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class MessageConfigurationService {

  private final MessageConfigurationRepository messageConfigurationRepository;

  public List<MessageConfiguration> getAllConfigurations() {
    return messageConfigurationRepository.findAll();
  }

  public MessageConfiguration findByApplication(String application) {
    return messageConfigurationRepository.findByApplication(application);
  }

  public void updateByApplication(MessageConfiguration configuration) {
    messageConfigurationRepository.save(configuration);
  }

  public void importAll(Collection<MessageConfiguration> messageConfigurations) {
    if (messageConfigurations != null && !messageConfigurations.isEmpty()) {
      messageConfigurations.stream().filter(Objects::nonNull).forEach(this::importOne);
    }
  }

  // save configuration only if it is new
  public void importOne(MessageConfiguration propertyMessageConfiguration) {
    if (!messageConfigurationRepository.existsById(propertyMessageConfiguration.getApplication())) {
      log.info("New configuration saved for: " + propertyMessageConfiguration.getApplication());
      messageConfigurationRepository.save(propertyMessageConfiguration);
    } else {
      log.info(
          "Configuration already exists in db for: "
              + propertyMessageConfiguration.getApplication());
    }
  }

  public EmailConfig fetchEmailConfig(String application, String subcode, String salesOrgCode) {
    SalesOrg salesOrgEntity = fetchSalesOrg(application, subcode, salesOrgCode);
    return salesOrgEntity != null ? salesOrgEntity.getEmailConfig() : null;
  }

  public SmsConfig fetchSmsConfig(String application, String subcode, String salesOrgCode) {
    SalesOrg salesOrgEntity = fetchSalesOrg(application, subcode, salesOrgCode);
    return salesOrgEntity != null ? salesOrgEntity.getSmsConfig() : null;
  }

  public SalesOrg fetchSalesOrg(String application, String subcode, String salesOrgCode) {
    Subcode subcodeEntity = null;
    SalesOrg salesOrgEntity = null;

    if (application != null) {
      MessageConfiguration messageConfiguration = findByApplication(application);

      if (messageConfiguration != null) {

        if (messageConfiguration.getSubcodes() != null
            && !messageConfiguration.getSubcodes().isEmpty()) {

          if (subcode != null) {
            subcodeEntity = messageConfiguration.getSubcodes().get(subcode);
          }
          if (subcodeEntity == null) {
            subcodeEntity = messageConfiguration.getSubcodes().get(DEFAULT_KEYWORD);
          }
        }

        if (subcodeEntity != null
            && subcodeEntity.getSalesOrgs() != null
            && !subcodeEntity.getSalesOrgs().isEmpty()) {

          if (salesOrgCode != null) {
            salesOrgEntity = subcodeEntity.getSalesOrgs().get(salesOrgCode);
          }

          if (salesOrgEntity == null) {
            salesOrgEntity = subcodeEntity.getSalesOrgs().get(DEFAULT_KEYWORD);
          }
        }
      }
    }
    return salesOrgEntity;
  }
}
