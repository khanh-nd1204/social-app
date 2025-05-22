package com.social_service.service;

import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.request.PermissionRequest;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.PermissionResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PermissionService {

    PermissionResponse createPermission(PermissionRequest request) throws Exception;

    void updatePermission(PermissionRequest request) throws Exception;

    PageResponse<List<PermissionResponse>> searchPermissions(Specification<PermissionEntity> spec, Pageable pageable)
            throws Exception;

    PermissionResponse getPermissionById(Integer id) throws Exception;

    void deletePermission(Integer id) throws Exception;
}
