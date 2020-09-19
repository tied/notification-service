package com.carlsberg.cx.notification.web.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationExceptionEnum {

  //CONFIGS
  MISSING_APPLICATION_CONFIGURATION("NOT_001", "Missing Application Configuration Exception"),
  MONGO_CONNECTION_ERROR("NOT_002", "Database Connection Error"),
  INVALID_CONFIGURATION("NOT_003", "Invalid Configuration"),
  EMPTY_USAGE_PROPERTY("NOT_004", "Missing 'usage' property value"),
  INVALID_USAGE_PROPERTY("NOT_005", "Invalid 'usage' property value"),

  //EMAIL
  EMAIL_IDENTIFIER_MISMATCH("NOT_101", "Email Identifier Mismatch"),
  EMAIL_NOT_READY("NOT_102", "Email Not Ready"),
  EMAIL_NOT_SENT("NOT_103", "Unable to Send Email"),
  ILLEGAL_EMAIL_MODIFICATION("NOT_104", "Illegal Email Modification"),
  INVALID_EMAIL_ID("NOT_105", "Invalid Email Identifier"),
  DUPLICATE_EMAIL_ADDRESS("NOT_106", "Duplicate Email Addresses Exception"),
  DUPLICATE_EMAIL_ATTACHMENT_CONTENT_ID("NOT_107", "Duplicate Email Attachment Content Id"),
  EMPTY_EMAIL_ATTACHMENT_CONTENT_ID("NOT_108", "Attachment with Empty Content Id"),
  EMPTY_EMAIL_ATTACHMENTS("NOT_109", "No Email Attachments"),
  INVALID_EMAIL_ATTACHMENT_ID("NOT_110", "Invalid Email Attachment Identifier"),
  EMAIL_ATTACHMENT_ERROR("NOT_111", "Error when adding an email attachment"),
  FAILED_EMAIL_SEND_ATTEMPT("NOT_112", "Failed Attempt to send email"),
  EXCEEDED_EMAIL_SEND_RETRIES("NOT_113", "Exceeded the maximum number of allowed retries."),
  MISSING_EMAIL_CONFIGURATION("NOT_114", "Unable to find configuration for email"),

  //SMS
  SMS_IDENTIFIER_MISMATCH("NOT_201", "Sms Identifier Mismatch"),
  SMS_NOT_READY("NOT_202", "Sms Not Ready"),
  SMS_NOT_SENT("NOT_203", "Unable to Send Sms"),
  ILLEGAL_SMS_MODIFICATION("NOT_204", "Illegal Sms Modification"),
  INVALID_SMS_ID("NOT_205", "Invalid Sms Identifier"),
  FAILED_SMS_SEND_ATTEMPT("NOT_206", "Failed Attempt to send sms"),
  EXCEEDED_SMS_SEND_RETRIES("NOT_207", "Exceeded the maximum number of allowed retries."),
  MISSING_SMS_CONFIGURATION("NOT_208", "Unable to find configuration for sms");

  private final String code;
  private final String name;
}
