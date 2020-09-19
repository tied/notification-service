package com.carlsberg.cx.notification.services.email;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.data.documents.MessageConfiguration.Subcode.SalesOrg.EmailConfig;
import com.carlsberg.cx.notification.web.exceptions.EmailSendException;

public interface EmailClient {

  public void sendEmail(Email email, EmailConfig emailConfig) throws EmailSendException;
}
