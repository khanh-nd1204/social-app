package com.social_service.controller;

import com.social_service.constant.Message;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.request.RoleRequest;
import com.social_service.model.response.ApiResponse;
import com.social_service.service.RoleService;
import com.social_service.util.ApiResponseUtil;
import com.social_service.util.Translator;
import com.social_service.validation.group.OnCreate;
import com.social_service.validation.group.OnUpdate;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Role Controller")
@SecurityRequirement(name = "bearerToken")
public class RoleController {

    RoleService roleService;

    @Operation(summary = "Tạo mới vai trò", description = "Tạo mới một vai trò vào hệ thống")
    @PostMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> create(@Validated(OnCreate.class) @RequestBody RoleRequest request)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                Translator.toLocale(Message.ROLE_CREATE_SUCCESS.getKey(), null),
                roleService.createRole(request)
        );
    }

    @Operation(summary = "Cập nhật vai trò", description = "Cập nhật thông tin của một vai trò")
    @PatchMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> update(@Validated(OnUpdate.class) @RequestBody RoleRequest request)
            throws Exception {
        roleService.updateRole(request);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.NO_CONTENT,
                Translator.toLocale(Message.ROLE_UPDATE_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Tìm kiếm vai trò", description = "Tìm kiếm và lọc danh sách vai trò")
    @GetMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> search(@Filter Specification<RoleEntity> spec, Pageable pageable)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.ROLE_SEARCH_SUCCESS.getKey(), null),
                roleService.searchRoles(spec, pageable)
        );
    }

    @Operation(summary = "Lấy thông tin vai trò", description = "Lấy thông tin chi tiết của một vai trò theo ID")
    @GetMapping(path = "/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> getById(@PathVariable Integer id) throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.ROLE_GET_SUCCESS.getKey(), null),
                roleService.getRoleById(id)
        );
    }

    @Operation(summary = "Xóa vai trò", description = "Xóa một vai trò theo ID")
    @DeleteMapping(path = "/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) throws Exception {
        roleService.deleteRole(id);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.RESET_CONTENT,
                Translator.toLocale(Message.ROLE_DELETE_SUCCESS.getKey(), null),
                null
        );
    }
}
