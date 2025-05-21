package com.social_service.repository;

import com.social_service.model.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer>, JpaSpecificationExecutor<PermissionEntity> {

    boolean existsByApiPathAndMethod(String apiPath, String method);

    boolean existsByName(String name);

    Optional<PermissionEntity> findByName(String name);

    Optional<PermissionEntity> findByApiPathAndMethod(String apiPath, String method);
}
