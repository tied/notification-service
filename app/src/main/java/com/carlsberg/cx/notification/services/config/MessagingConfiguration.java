package com.carlsberg.cx.notification.services.config;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration;
import com.carlsberg.cx.notification.services.properties.EmailProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationSubCodeProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationSubCodeProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.web.config.WebDataMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/** Converts property files into an entity to store in the db */
@Getter
@Configuration
public class MessagingConfiguration {

  private final EmailConfiguration emailConfiguration;
  private final SmsConfiguration smsConfiguration;
  private final WebDataMapper webDataMapper;
  private final MessageConfigurationService messageConfigurationService;

  @Autowired
  public MessagingConfiguration(
      EmailConfiguration emailConfiguration,
      SmsConfiguration smsConfiguration,
      WebDataMapper webDataMapper,
      MessageConfigurationService messageConfigurationService) {

    this.emailConfiguration = emailConfiguration;
    this.smsConfiguration = smsConfiguration;
    this.webDataMapper = webDataMapper;
    this.messageConfigurationService = messageConfigurationService;

    importPropertyConfigurations();
  }

  public void importPropertyConfigurations() {
    EmailProperties emailProperties = getEmailConfiguration().getEmailProperties();
    SmsProperties smsProperties = getSmsConfiguration().getSmsProperties();
    Map<String, MessageConfiguration> configurations = new HashMap<>();

    // convert email properties to configurations
    if (emailProperties != null
        && emailProperties.getApplications() != null
        && !emailProperties.getApplications().isEmpty()) {

      emailProperties
          .getApplications()
          .keySet()
          .forEach(
              applicationName -> {
                MessageConfiguration messageConfiguration =
                    configurations.computeIfAbsent(
                        applicationName,
                        app -> MessageConfiguration.builder().application(applicationName).build());

                EmailApplicationProperties applicationProperties =
                    emailProperties.getApplications().get(applicationName);

                if (applicationProperties != null
                    && applicationProperties.getSubCodes() != null
                    && !applicationProperties.getSubCodes().isEmpty()) {
                  applicationProperties
                      .getSubCodes()
                      .keySet()
                      .forEach(
                          subcodeName -> {
                            Subcode subcode =
                                messageConfiguration
                                    .getSubcodes()
                                    .computeIfAbsent(subcodeName, sub -> Subcode.builder().build());

                            EmailApplicationSubCodeProperties subCodeProperties =
                                applicationProperties.getSubCodes().get(subcodeName);

                            if (subCodeProperties != null
                                && subCodeProperties.getSalesOrgs() != null
                                && !subCodeProperties.getSalesOrgs().isEmpty()) {
                              subCodeProperties
                                  .getSalesOrgs()
                                  .keySet()
                                  .forEach(
                                      salesOrgName -> {
                                        SalesOrg salesOrg =
                                            subcode
                                                .getSalesOrgs()
                                                .computeIfAbsent(
                                                    salesOrgName,
                                                    sorg -> SalesOrg.builder().build());

                                        EmailApplicationSubCodeSalesOrgProperties
                                            salesOrgProperties =
                                                subCodeProperties.getSalesOrgs().get(salesOrgName);

                                        if (salesOrgProperties != null) {
                                          salesOrg.setEmailConfig(
                                              webDataMapper.toConfig(salesOrgProperties));
                                        }
                                      });
                            }
                          });
                }
              });
    }

    // convert sms properties to configurations
    if (smsProperties != null
        && smsProperties.getApplications() != null
        && !smsProperties.getApplications().isEmpty()) {

      smsProperties
          .getApplications()
          .keySet()
          .forEach(
              applicationName -> {
                MessageConfiguration messageConfiguration =
                    configurations.computeIfAbsent(
                        applicationName,
                        app -> MessageConfiguration.builder().application(applicationName).build());

                SmsApplicationProperties applicationProperties =
                    smsProperties.getApplications().get(applicationName);

                if (applicationProperties != null
                    && applicationProperties.getSubCodes() != null
                    && !applicationProperties.getSubCodes().isEmpty()) {
                  applicationProperties
                      .getSubCodes()
                      .keySet()
                      .forEach(
                          subcodeName -> {
                            Subcode subcode =
                                messageConfiguration
                                    .getSubcodes()
                                    .computeIfAbsent(subcodeName, sub -> Subcode.builder().build());

                            SmsApplicationSubCodeProperties subCodeProperties =
                                applicationProperties.getSubCodes().get(subcodeName);

                            if (subCodeProperties != null
                                && subCodeProperties.getSalesOrgs() != null
                                && !subCodeProperties.getSalesOrgs().isEmpty()) {
                              subCodeProperties
                                  .getSalesOrgs()
                                  .keySet()
                                  .forEach(
                                      salesOrgName -> {
                                        SalesOrg salesOrg =
                                            subcode
                                                .getSalesOrgs()
                                                .computeIfAbsent(
                                                    salesOrgName,
                                                    sorg -> SalesOrg.builder().build());

                                        SmsApplicationSubCodeSalesOrgProperties salesOrgProperties =
                                            subCodeProperties.getSalesOrgs().get(salesOrgName);

                                        if (salesOrgProperties != null) {
                                          salesOrg.setSmsConfig(
                                              webDataMapper.toConfig(salesOrgProperties));
                                        }
                                      });
                            }
                          });
                }
              });
    }

    // check if configurations already exist in the db by application name
    messageConfigurationService.importAll(configurations.values());
  }
}
