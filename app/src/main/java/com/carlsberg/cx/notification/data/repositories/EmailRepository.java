package com.carlsberg.cx.notification.data.repositories;

import com.carlsberg.cx.notification.data.documents.Email;
import com.carlsberg.cx.notification.web.entities.enums.MessageStatusEnum;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {

  Email findByEidAndValidToIsNull(String eid);

  List<Email> findTop20ByStatusAndValidToIsNullOrderByScheduledAtAsc(MessageStatusEnum messageStatusEnum);

}
