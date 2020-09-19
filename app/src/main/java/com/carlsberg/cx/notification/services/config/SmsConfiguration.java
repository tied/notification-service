package com.carlsberg.cx.notification.services.config;

import com.carlsberg.cx.notification.services.properties.SmsProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationSubCodeProperties;
import com.carlsberg.cx.notification.services.properties.SmsProperties.SmsApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.web.exceptions.InvalidConfigurationException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Getter
@Configuration
public class SmsConfiguration {

  private final SmsProperties smsProperties;

  @Autowired
  public SmsConfiguration(SmsProperties smsProperties) {
    this.smsProperties = smsProperties;
    validateProperties();
  }

  public void validateProperties() {
    List<String> errorMessages = new ArrayList<>();

    if (getSmsProperties() != null) {
      if (getSmsProperties().getApplications() != null
          && !getSmsProperties().getApplications().isEmpty()) {

        for (String appName : getSmsProperties().getApplications().keySet()) {
          SmsApplicationProperties applicationProperties =
              getSmsProperties().getApplications().get(appName);

          if (applicationProperties != null){

            if (applicationProperties.getSubCodes() != null && !applicationProperties.getSubCodes().isEmpty()){
              for (String subCode : applicationProperties.getSubCodes().keySet()) {

                SmsApplicationSubCodeProperties subCodeProperties =
                    applicationProperties.getSubCodes().get(subCode);

                if (subCodeProperties != null){

                  if (subCodeProperties.getSalesOrgs() != null && !subCodeProperties.getSalesOrgs().isEmpty()){

                    for (String salesOrg : subCodeProperties.getSalesOrgs().keySet()){

                      SmsApplicationSubCodeSalesOrgProperties salesOrgProperties =
                          subCodeProperties.getSalesOrgs().get(salesOrg);

                      if (salesOrgProperties == null){
                        errorMessages.add(
                            String.format("No Sms application '%s' subCode '%s' salesOrg '%s' property value defined.",
                                appName, subCode, salesOrg));
                      }

                      if (salesOrgProperties != null && StringUtils.isEmpty(salesOrgProperties.getUsage())){
                        errorMessages.add(
                            String.format("No 'usage' property exists for: Sms application '%s' subCode '%s' salesOrg '%s'.",
                                appName, subCode, salesOrg));
                      }
                    }
                  } else {
                    errorMessages.add(
                        String.format("No Sms application '%s' subCode '%s' salesOrgs property defined.",
                            appName, subCode));
                  }
                } else {
                  errorMessages.add(
                      String.format("No Sms application '%s' subCode '%s' property value defined.",
                          appName, subCode));
                }
              }
            } else {
              errorMessages.add(
                String.format("No Sms Application SubCodes property defined for: '%s'.", appName));
            }
          } else {
            errorMessages.add(
                String.format("No Sms application property defined for '%s'.", appName));
          }
        }
      } else {
        errorMessages.add("No Sms application properties defined.");
      }
    } else {
      errorMessages.add("No Sms properties exist!");
    }

    if (!errorMessages.isEmpty()){
      throw new InvalidConfigurationException("sms", errorMessages);
    }
  }
}
