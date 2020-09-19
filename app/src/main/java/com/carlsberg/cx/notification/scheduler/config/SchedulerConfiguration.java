package com.carlsberg.cx.notification.scheduler.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Builder.Default;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {

  @Bean
  public ExecutorService executorService() {
    return Executors.newFixedThreadPool(5);
  }
}
