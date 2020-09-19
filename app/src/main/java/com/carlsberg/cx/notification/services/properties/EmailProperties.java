package com.carlsberg.cx.notification.services.properties;

import com.carlsberg.cx.notification.services.config.MailTrapProperties;
import com.carlsberg.cx.notification.web.exceptions.MissingApplicationConfigurationException;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("notification.email")
public class EmailProperties {

  private Map<String, EmailApplicationProperties> applications = new HashMap<>();

  public EmailApplicationSubCodeSalesOrgProperties fetch(
      String appName, String subCode, String salesOrg) {

    if (getApplications() != null) {
      EmailApplicationProperties applicationProperties =
          getApplications().get(appName);

      if (applicationProperties != null) {

        EmailApplicationSubCodeProperties subCodeProperties =
            applicationProperties.getOrDefault(subCode);

        if (subCodeProperties != null) {

          EmailApplicationSubCodeSalesOrgProperties salesOrgProperties =
              subCodeProperties.getOrDefault(salesOrg);

          if (salesOrgProperties != null) {
            return salesOrgProperties;
          }
        }
      }
    }

    throw new MissingApplicationConfigurationException("email", appName, subCode, salesOrg);
  }

  @Data
  public static class EmailApplicationProperties {
    private Map<String, EmailApplicationSubCodeProperties> subCodes;

    public EmailApplicationSubCodeProperties getOrDefault(String subCode) {
      EmailApplicationSubCodeProperties properties = null;

      if (getSubCodes() != null) {
        if (subCode != null) {
          properties = getSubCodes().get(subCode);
        }
        if (properties == null) {
          properties = getSubCodes().get("DEFAULT");
        }
      }
      return properties;
    }
  }

  @Data
  public static class EmailApplicationSubCodeProperties {
    private Map<String, EmailApplicationSubCodeSalesOrgProperties> salesOrgs;

    public EmailApplicationSubCodeSalesOrgProperties getOrDefault(String salesOrg) {
      EmailApplicationSubCodeSalesOrgProperties properties = null;

      if (getSalesOrgs() != null) {
        if (salesOrg != null) {
          properties = getSalesOrgs().get(salesOrg);
        }
        if (properties == null) {
          properties = getSalesOrgs().get("DEFAULT");
        }
      }

      return properties;
    }
  }

  @Data
  public static class EmailApplicationSubCodeSalesOrgProperties implements MailTrapProperties {
    private String usage;
    private String emailAddress;

    private String mailtrapUsername;
    private String mailtrapPassword;

    private String sendgridKey;
  }
}
