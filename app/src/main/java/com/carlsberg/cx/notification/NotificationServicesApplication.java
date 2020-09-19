package com.carlsberg.cx.notification;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationServicesApplication {

  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    System.setProperty("mail.mime.charset", "utf8");
    SpringApplication.run(NotificationServicesApplication.class, args);
  }

}
