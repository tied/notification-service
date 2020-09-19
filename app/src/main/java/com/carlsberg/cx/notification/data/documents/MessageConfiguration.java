package com.carlsberg.cx.notification.data.documents;

import com.carlsberg.cx.notification.services.config.MailTrapProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("configuration")
@NoArgsConstructor
@AllArgsConstructor
public class MessageConfiguration {

  /** The name of the application */
  @Id private String application;

  /** The available subcode scopes */
  @Builder.Default private Map<String, Subcode> subcodes = new HashMap<>();

  @Data
  @Builder
  @Document
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Subcode {

    /** The available sales org groups */
    @Builder.Default private Map<String, SalesOrg> salesOrgs = new HashMap<>();

    @Data
    @Builder
    @Document
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesOrg {

      /** The details for sms communication */
      private SmsConfig smsConfig;

      /** The details for email communication */
      private EmailConfig emailConfig;

      @Data
      @Builder
      @Document
      @NoArgsConstructor
      @AllArgsConstructor
      public static class SmsConfig implements MailTrapProperties {

        /** The gateway to use (e.g. mailtrap/twilio) */
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
      @Document
      @NoArgsConstructor
      @AllArgsConstructor
      public static class EmailConfig implements MailTrapProperties {

        /** The gateway to use (e.g. mailtrap/twilio) */
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
