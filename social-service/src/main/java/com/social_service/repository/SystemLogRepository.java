package com.social_service.repository;

import com.social_service.model.entity.SystemLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLogEntity, Integer>, JpaSpecificationExecutor<SystemLogEntity> {
    void deleteByCreatedAtBefore(Instant instant);
}
