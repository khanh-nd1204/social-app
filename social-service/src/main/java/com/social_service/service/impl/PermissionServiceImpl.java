package com.social_service.service.impl;

import com.social_service.constant.Message;
import com.social_service.constant.Role;
import com.social_service.exception.NotFoundException;
import com.social_service.mapper.PermissionMapper;
import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.request.PermissionRequest;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.PermissionResponse;
import com.social_service.repository.PermissionRepository;
import com.social_service.repository.RoleRepository;
import com.social_service.service.PermissionService;
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
@Slf4j(topic = "PERMISSION-SERVICE")
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    SystemLogService systemLogService;

    RoleRepository roleRepository;

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionRequest request) throws Exception {
        log.info("Creating request: {}", request.toString());

        if (permissionRepository.existsByName(request.getName())) {
            throw new BadRequestException(Translator.toLocale(Message.PERMISSION_NAME_EXISTS.getKey(), null));
        }

        if (permissionRepository.existsByApiPathAndMethod(request.getApiPath(), request.getMethod())) {
            throw new BadRequestException(Translator.toLocale(Message.PERMISSION_EXISTS.getKey(), null));
        }

        PermissionEntity permission = permissionMapper.toEntity(request);

        try {
            permissionRepository.save(permission);

            RoleEntity roleAdmin = roleRepository.findByName(Role.ADMIN.getName()).orElse(null);
            if (roleAdmin != null) {
                List<PermissionEntity> permissions = roleAdmin.getPermissions();
                permissions.add(permission);
                roleAdmin.setPermissions(permissions);
                roleRepository.save(roleAdmin);
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    Translator.toLocale(Message.PERMISSION_EXISTS.getKey(), null)
            );
        }

        systemLogService.createLog(
                permission.getId(), Message.CREATE.getKey(), Message.PERMISSION_CREATE_SUCCESS.getKey()
        );

        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional
    public void updatePermission(PermissionRequest request) throws Exception {
        log.info("Updating request: {}", request.toString());

        PermissionEntity permission = permissionRepository.findById(request.getId()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.PERMISSION_NOT_FOUND.getKey(), null))
        );

        PermissionEntity permissionByName = permissionRepository.findByName(request.getName()).orElse(null);
        if (permissionByName != null && !permissionByName.getId().equals(permission.getId())) {
            throw new BadRequestException(Translator.toLocale(Message.PERMISSION_NAME_EXISTS.getKey(), null));
        }

        PermissionEntity permissionByApiPathAndMethod =
                permissionRepository.findByApiPathAndMethod(request.getApiPath(), request.getMethod()).orElse(null);
        if (permissionByApiPathAndMethod != null && !permissionByApiPathAndMethod.getId().equals(permission.getId())) {
            throw new BadRequestException(Translator.toLocale(Message.PERMISSION_EXISTS.getKey(), null));
        }

        permissionMapper.updateEntity(permission, request);
        permissionRepository.save(permission);

        systemLogService.createLog(permission.getId(), Message.UPDATE.getKey(), Message.PERMISSION_UPDATE_SUCCESS.getKey());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<List<PermissionResponse>> searchPermissions(Specification<PermissionEntity> spec, Pageable pageable) throws Exception {
        log.info("Searching permission: {}", spec.toString());

        Page<PermissionEntity> pageData = permissionRepository.findAll(spec, pageable);

        return PageResponse.<List<PermissionResponse>>builder()
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .result(pageData.getContent().stream().map(permissionMapper::toResponse).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Integer id) throws Exception {
        log.info("Retrieving permission {}", id);

        PermissionEntity permission = permissionRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.PERMISSION_NOT_FOUND.getKey(), null)));

        return permissionMapper.toResponse(permission);
    }

    @Override
    public void deletePermission(Integer id) throws Exception {
        log.info("Deleting permission {}", id);

        PermissionEntity permission = permissionRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.PERMISSION_NOT_FOUND.getKey(), null)));

        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        }

        permissionRepository.deleteById(id);

        log.info("Permission {} deleted", permission.getName());

        systemLogService.createLog(
                permission.getId(), Message.DELETE.getKey(), Message.PERMISSION_DELETE_SUCCESS.getKey()
        );
    }
}
