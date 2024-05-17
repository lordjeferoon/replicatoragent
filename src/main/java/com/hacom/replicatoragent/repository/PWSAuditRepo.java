package com.hacom.replicatoragent.repository;

import com.hacom.replicatoragent.model.PWSAudit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PWSAuditRepo extends ReactiveMongoRepository<PWSAudit, String> {

}