package com.social_service.repository;

import com.social_service.constant.EmailStatus;
import com.social_service.model.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailEntity, Integer> {

    Optional<List<EmailEntity>> findByStatusAndDurationIsBefore(EmailStatus status, Instant duration);

    Optional<List<EmailEntity>> findByStatusOrDurationIsAfter(EmailStatus status, Instant duration);

    Boolean existsByRecipientAndStatusAndDurationIsBefore(String recipient, EmailStatus status, Instant duration);
}
