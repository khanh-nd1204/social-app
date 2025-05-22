package com.social_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionEntity extends BaseEntity {
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

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    List<RoleEntity> roles;
}
