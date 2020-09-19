package com.carlsberg.cx.notification.services.sms;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.SmsConfig;
import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.web.exceptions.SmsSendException;

public interface SmsClient {

  public void sendSms(Sms sms, SmsConfig smsConfig) throws SmsSendException;

}
