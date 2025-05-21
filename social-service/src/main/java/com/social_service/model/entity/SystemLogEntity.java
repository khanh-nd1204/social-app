package com.social_service.model.entity;

import com.social_service.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "system_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemLogEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "action", nullable = false, updatable = false, length = 100)
    String action;

    @Column(name = "description", nullable = false, updatable = false, length = 100)
    String description;

    @Column(name = "params", nullable = false, updatable = false, length = 100)
    String params;

    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @Column(name = "created_by", updatable = false)
    String createdBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("SYSTEM");
    }
}
