package com.social_service.controller;

import com.social_service.constant.Message;
import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.request.PermissionRequest;
import com.social_service.model.response.ApiResponse;
import com.social_service.service.PermissionService;
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
@RequestMapping("permissions")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Permission Controller")
@SecurityRequirement(name = "bearerToken")
public class PermissionController {

    PermissionService permissionService;

    @Operation(summary = "Tạo mới quyền hạn", description = "Tạo mới một quyền hạn vào hệ thống")
    @PostMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> create(@Validated(OnCreate.class) @RequestBody PermissionRequest request)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                Translator.toLocale(Message.PERMISSION_CREATE_SUCCESS.getKey(), null),
                permissionService.createPermission(request)
        );
    }

    @Operation(summary = "Cập nhật quyền hạn", description = "Cập nhật thông tin của một quyền hạn")
    @PatchMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> update(@Validated(OnUpdate.class) @RequestBody PermissionRequest request)
            throws Exception {
        permissionService.updatePermission(request);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.NO_CONTENT,
                Translator.toLocale(Message.PERMISSION_UPDATE_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Tìm kiếm quyền hạn", description = "Tìm kiếm và lọc danh sách quyền hạn")
    @GetMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> search(@Filter Specification<PermissionEntity> spec, Pageable pageable)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.PERMISSION_SEARCH_SUCCESS.getKey(), null),
                permissionService.searchPermissions(spec, pageable)
        );
    }

    @Operation(summary = "Lấy thông tin quyền hạn", description = "Lấy thông tin chi tiết của một quyền hạn theo ID")
    @GetMapping(path = "/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> getById(@PathVariable Integer id) throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.PERMISSION_GET_SUCCESS.getKey(), null),
                permissionService.getPermissionById(id)
        );
    }

    @Operation(summary = "Xóa quyền hạn", description = "Xóa một quyền hạn theo ID")
    @DeleteMapping(path = "/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Integer id) throws Exception {
        permissionService.deletePermission(id);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.RESET_CONTENT,
                Translator.toLocale(Message.PERMISSION_DELETE_SUCCESS.getKey(), null),
                null
        );
    }
}
