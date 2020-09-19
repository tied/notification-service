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
@ConfigurationProperties("notification.sms")
public class SmsProperties {

  private Map<String, SmsApplicationProperties> applications = new HashMap<>();

  public SmsApplicationSubCodeSalesOrgProperties fetch(
      String appName, String subCode, String salesOrg) {

    if (getApplications() != null) {
      SmsApplicationProperties applicationProperties =
          getApplications().get(appName);

      if (applicationProperties != null) {

        SmsApplicationSubCodeProperties subCodeProperties =
            applicationProperties.getOrDefault(subCode);

        if (subCodeProperties != null) {

          SmsApplicationSubCodeSalesOrgProperties salesOrgProperties =
              subCodeProperties.getOrDefault(salesOrg);

          if (salesOrgProperties != null) {
            return salesOrgProperties;
          }
        }
      }
    }

    throw new MissingApplicationConfigurationException("sms", appName, subCode, salesOrg);
  }

  @Data
  public static class SmsApplicationProperties {
    private Map<String, SmsApplicationSubCodeProperties> subCodes;

    public SmsApplicationSubCodeProperties getOrDefault(String subCode) {
      SmsApplicationSubCodeProperties properties = null;

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
  public static class SmsApplicationSubCodeProperties {
    private Map<String, SmsApplicationSubCodeSalesOrgProperties> salesOrgs;

    public SmsApplicationSubCodeSalesOrgProperties getOrDefault(String salesOrg) {
      SmsApplicationSubCodeSalesOrgProperties properties = null;

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
  public static class SmsApplicationSubCodeSalesOrgProperties implements MailTrapProperties {
    private String usage;
    private String phoneNumber;

    private String mailtrapUsername;
    private String mailtrapPassword;

    private String twilioAccountSid;
    private String twilioAuthToken;
  }
}
