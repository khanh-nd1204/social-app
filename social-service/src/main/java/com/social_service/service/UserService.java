package com.social_service.service;

import com.social_service.model.entity.UserEntity;
import com.social_service.model.request.UserRequest;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request) throws Exception;

    void updateUser(UserRequest request) throws Exception;

    UserResponse getUserById(String id) throws Exception;

    PageResponse<List<UserResponse>> searchUsers(Specification<UserEntity> spec, Pageable pageable) throws Exception;

    void lockUser(String id) throws Exception;

    void unLockUser(String id) throws Exception;

    UserEntity getUserByEmail(String email);

    void updateToken(String id, String token);

    UserEntity getUserByEmailAndRefreshToken(String email, String refreshToken);

    void changePassword(UserRequest request) throws Exception;
}
