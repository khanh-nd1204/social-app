package com.social_service.mapper;

import com.social_service.model.entity.UserEntity;
import com.social_service.model.request.UserRequest;
import com.social_service.model.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity entity) {
        if (entity == null)
            return null;

        return UserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .birthDate(entity.getBirthDate())
                .verified(entity.getVerified())
                .gender(entity.getGender())
                .bio(entity.getBio())
                .avatar(entity.getAvatar())
                .active(entity.getActive())
                .roleName(entity.getRole() != null ? entity.getRole().getName() : null)
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public UserEntity toEntity(UserRequest request) {
        if (request == null)
            return null;

        return UserEntity.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .address(request.getAddress())
                .phone(request.getPhone())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .bio(request.getBio())
                .avatar(request.getAvatar())
                .build();
    }

    public List<UserResponse> toResponses(List<UserEntity> entities) {
        if (entities == null || entities.isEmpty())
            return Collections.emptyList();

        return entities.stream()
                .map(this::toResponse).toList();
    }

    public List<UserEntity> toEntities(List<UserRequest> requests) {
        if (requests == null || requests.isEmpty())
            return Collections.emptyList();

        return requests.stream().map(this::toEntity).toList();
    }

    public void updateEntity(UserEntity entity, UserRequest request) {
        if (request == null) {
            return;
        }

        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
        }
        if (request.getPhone() != null) {
            entity.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            entity.setGender(request.getGender());
        }
        if (request.getBirthDate() != null) {
            entity.setBirthDate(request.getBirthDate());
        }
        if (request.getBio() != null) {
            entity.setBio(request.getBio());
        }
        if (request.getAvatar() != null) {
            entity.setAvatar(request.getAvatar());
        }
    }
}

