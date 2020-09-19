package com.carlsberg.cx.notification.web.entities.inbound;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SMS/Email Provider App configuration */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMessageConfigurationDTO {

  /** The name of the application */
  @NotBlank
  private String application;

  /** The available subcode scopes */
  @NotEmpty
  private Map<String, SubcodeDTO> subcodes = new HashMap<>();

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SubcodeDTO {

    /** The available sales org groups */
    @Builder.Default
    @NotEmpty
    private Map<String, SalesOrgDTO> salesOrgs = new HashMap<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesOrgDTO {

      /** The details for sms communication */
      private SmsConfigDTO smsConfig;

      /** The details for email communication */
      private EmailConfigDTO emailConfig;

      @Data
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      public static class SmsConfigDTO {

        /** The gateway to use (e.g. mailtrap/twilio) */
        @NotEmpty
        private String usage;

        /** The sender phone number */
        private String phoneNumber;

        /** Mailtrap's username */
        private String mailtrapUsername;

        /** Mailtrap's password */
        private String mailtrapPassword;

        /** Twilio Account Sid */
        private String accountSid;

        /** Twilio authentication token */
        private String authToken;
      }

      @Data
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      public static class EmailConfigDTO {

        /** The gateway to use (e.g. mailtrap/twilio) */
        @NotEmpty
        private String usage;

        /** The sender email address */
        private String emailAddress;

        /** Mailtrap's username */
        private String mailtrapUsername;

        /** Mailtrap's password */
        private String mailtrapPassword;

        /** Sendgrid Api Key */
        private String sendgridKey;
      }
    }
  }
}

