package com.social_service.mapper;

import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.request.RoleRequest;
import com.social_service.model.response.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RoleMapper {

    public RoleResponse toResponse(RoleEntity entity) {
        if (entity == null)
            return null;

        return RoleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .permissionIds(entity.getPermissions().stream().map(PermissionEntity::getId).toList())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public RoleEntity toEntity(RoleRequest request) {
        if (request == null)
            return null;

        return RoleEntity.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public List<RoleResponse> toResponses(List<RoleEntity> entities) {
        if (entities == null || entities.isEmpty())
            return Collections.emptyList();

        return entities.stream().map(this::toResponse).toList();
    }

    public List<RoleEntity> toEntities(List<RoleRequest> requests) {
        if (requests == null || requests.isEmpty())
            return Collections.emptyList();

        return requests.stream()
                .map(this::toEntity).toList();
    }

    public void updateEntity(RoleEntity entity, RoleRequest request) {
        if (request == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
    }
}

