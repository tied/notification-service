package com.carlsberg.cx.notification.data.repositories;

import com.carlsberg.cx.notification.data.documents.MessageConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageConfigurationRepository extends MongoRepository<MessageConfiguration, String> {

  MessageConfiguration findByApplication(String application);

}
