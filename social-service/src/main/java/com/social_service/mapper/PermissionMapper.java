package com.social_service.mapper;

import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.request.PermissionRequest;
import com.social_service.model.response.PermissionResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(PermissionEntity entity) {
        if (entity == null)
            return null;

        return PermissionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .apiPath(entity.getApiPath())
                .method(entity.getMethod())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public PermissionEntity toEntity(PermissionRequest request) {
        if (request == null)
            return null;

        return PermissionEntity.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .apiPath(request.getApiPath())
                .method(request.getMethod())
                .build();
    }

    public List<PermissionResponse> toResponses(List<PermissionEntity> entities) {
        if (entities == null || entities.isEmpty())
            return Collections.emptyList();

        return entities.stream().map(this::toResponse).toList();
    }

    public List<PermissionEntity> toEntities(List<PermissionRequest> requests) {
        if (requests == null || requests.isEmpty())
            return Collections.emptyList();

        return requests.stream().map(this::toEntity).toList();
    }

    public void updateEntity(PermissionEntity entity, PermissionRequest request) {
        if (request == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getApiPath() != null) {
            entity.setApiPath(request.getApiPath());
        }
        if (request.getMethod() != null) {
            entity.setMethod(request.getMethod());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
    }
}

