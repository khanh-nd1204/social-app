package com.social_service.service.impl;

import com.social_service.constant.Message;
import com.social_service.constant.Role;
import com.social_service.exception.NotFoundException;
import com.social_service.mapper.RoleMapper;
import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.request.RoleRequest;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.RoleResponse;
import com.social_service.repository.PermissionRepository;
import com.social_service.repository.RoleRepository;
import com.social_service.service.RoleService;
import com.social_service.service.SystemLogService;
import com.social_service.util.Translator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "ROLE-SERVICE")
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    PermissionRepository permissionRepository;

    RoleMapper roleMapper;

    SystemLogService systemLogService;

    List<String> PROTECTED_ROLES = List.of(Role.ADMIN.getName(), Role.USER.getName());

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) throws Exception {
        log.info("Creating request: {}", request.toString());

        if (roleRepository.existsByName(request.getName())) {
            throw new BadRequestException(Translator.toLocale(Message.ROLE_NAME_EXISTS.getKey(), null));
        }

        RoleEntity role = roleMapper.toEntity(request);

        List<PermissionEntity> permissions = permissionRepository.findAllById(request.getPermissionIds());

        role.setPermissions(permissions);

        try {
            roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Translator.toLocale(Message.ROLE_EXISTS.getKey(), null));
        }

        systemLogService.createLog(role.getName(), Message.CREATE.getKey(), Message.ROLE_CREATE.getKey());

        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional
    public void updateRole(RoleRequest request) throws Exception {
        log.info("Updating request: {}", request.toString());

        RoleEntity role = roleRepository.findById(request.getId()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null))
        );

        RoleEntity roleByName = roleRepository.findByName(request.getName()).orElse(null);
        if (roleByName != null && !roleByName.getId().equals(role.getId())) {
            throw new BadRequestException(Translator.toLocale(Message.ROLE_NAME_EXISTS.getKey(), null));
        }

        roleMapper.updateEntity(role, request);

        List<PermissionEntity> permissions = permissionRepository.findAllById(request.getPermissionIds());

        role.setPermissions(permissions);

        roleRepository.save(role);

        systemLogService.createLog(role.getName(), Message.UPDATE.getKey(), Message.ROLE_UPDATE.getKey());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<List<RoleResponse>> searchRole(Specification<RoleEntity> spec, Pageable pageable) throws Exception {
        log.info("Searching role: {}", spec.toString());

        Page<RoleEntity> pageData = roleRepository.findAll(spec, pageable);

        return PageResponse.<List<RoleResponse>>builder()
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .result(pageData.getContent().stream().map(roleMapper::toResponse).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Integer id) throws Exception {
        log.info("Retrieving role {}", id);

        RoleEntity role = roleRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null)));

        return roleMapper.toResponse(role);
    }

    @Override
    public void deleteRole(Integer id) throws Exception {
        log.info("Deleting role {}", id);

        RoleEntity role = roleRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null))
        );

        if (PROTECTED_ROLES.contains(role.getName()) || role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new BadRequestException(Translator.toLocale(Message.DELETE_FAIL.getKey(), null));
        }

        roleRepository.deleteById(id);

        systemLogService.createLog(role.getName(), Message.DELETE.getKey(), Message.ROLE_DELETE.getKey());
    }
}
