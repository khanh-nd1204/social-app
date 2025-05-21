package com.social_service.service;

import com.social_service.model.entity.RoleEntity;
import com.social_service.model.request.RoleRequest;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.RoleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request) throws Exception;

    void updateRole(RoleRequest request) throws Exception;

    PageResponse<List<RoleResponse>> searchRole(Specification<RoleEntity> spec, Pageable pageable) throws Exception;

    RoleResponse getRoleById(Integer id) throws Exception;

    void deleteRole(Integer id) throws Exception;
}
