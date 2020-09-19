package com.carlsberg.cx.notification.data.repositories;

import com.carlsberg.cx.notification.data.documents.Sms;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRepository extends MongoRepository<Sms, String> {

  Sms findBySidAndValidToIsNull(String id);

  List<Sms> findTop20ByStatusAndValidToIsNullOrderByScheduledAtAsc(MessageStatusEnum messageStatus);

}
