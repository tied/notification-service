package com.carlsberg.cx.notification.services.config;

import com.carlsberg.cx.notification.services.properties.EmailProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationSubCodeProperties;
import com.carlsberg.cx.notification.services.properties.EmailProperties.EmailApplicationSubCodeSalesOrgProperties;
import com.carlsberg.cx.notification.web.exceptions.InvalidConfigurationException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Getter
@Configuration
public class EmailConfiguration {

  private final EmailProperties emailProperties;

  @Autowired
  public EmailConfiguration(EmailProperties emailProperties) {
    this.emailProperties = emailProperties;
    validateProperties();
  }

  public void validateProperties() {
    List<String> errorMessages = new ArrayList<>();

    if (getEmailProperties() != null) {
      if (getEmailProperties().getApplications() != null
          && !getEmailProperties().getApplications().isEmpty()) {

        for (String appName : getEmailProperties().getApplications().keySet()) {
          EmailApplicationProperties applicationProperties =
              getEmailProperties().getApplications().get(appName);

          if (applicationProperties != null){

            if (applicationProperties.getSubCodes() != null && !applicationProperties.getSubCodes().isEmpty()){
              for (String subCode : applicationProperties.getSubCodes().keySet()) {

                EmailApplicationSubCodeProperties subCodeProperties =
                    applicationProperties.getSubCodes().get(subCode);

                if (subCodeProperties != null){

                  if (subCodeProperties.getSalesOrgs() != null && !subCodeProperties.getSalesOrgs().isEmpty()){

                    for (String salesOrg : subCodeProperties.getSalesOrgs().keySet()){

                      EmailApplicationSubCodeSalesOrgProperties salesOrgProperties =
                          subCodeProperties.getSalesOrgs().get(salesOrg);

                      if (salesOrgProperties == null){
                        errorMessages.add(
                            String.format("No Email application '%s' subCode '%s' salesOrg '%s' property value defined.",
                                appName, subCode, salesOrg));
                      }

                      if (salesOrgProperties != null && StringUtils.isEmpty(salesOrgProperties.getUsage())){
                        errorMessages.add(
                            String.format("No 'usage' property exists for: Email application '%s' subCode '%s' salesOrg '%s'.",
                                appName, subCode, salesOrg));
                      }
                    }
                  } else {
                    errorMessages.add(
                        String.format("No Email application '%s' subCode '%s' salesOrgs property defined.",
                            appName, subCode));
                  }
                } else {
                  errorMessages.add(
                      String.format("No Email application '%s' subCode '%s' property value defined.",
                          appName, subCode));
                }
              }
            } else {
              errorMessages.add(
                String.format("No Email Application SubCodes property defined for: '%s'.", appName));
            }
          } else {
            errorMessages.add(
                String.format("No Email application property defined for '%s'.", appName));
          }
        }
      } else {
        errorMessages.add("No Email application properties defined.");
      }
    } else {
      errorMessages.add("No Email properties exist!");
    }

    if (!errorMessages.isEmpty()){
      throw new InvalidConfigurationException("email", errorMessages);
    }
  }

}
