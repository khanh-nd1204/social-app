package com.social_service.model.entity;

import com.social_service.constant.EmailStatus;
import com.social_service.constant.EmailType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "recipient", nullable = false, length = 100)
    String recipient;

    @Column(name = "subject", nullable = false, length = 100)
    String subject;

    @Column(name = "template", nullable = false, length = 100)
    String template;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    EmailStatus status;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    EmailType type;

    @Column(name = "duration")
    Instant duration;

    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
