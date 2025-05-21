package com.social_service.model.entity;

import com.social_service.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    String name;

    @Column(name = "api_path", nullable = false, length = 100)
    String apiPath;

    @Column(name = "method", nullable = false, length = 100)
    String method;

    @Column(name = "description", nullable = false, length = 100)
    String description;

    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @Column(name = "created_by", updatable = false)
    String createdBy;

    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "updated_by")
    String updatedBy;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    List<RoleEntity> roles;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("SYSTEM");
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("SYSTEM");
    }
}
