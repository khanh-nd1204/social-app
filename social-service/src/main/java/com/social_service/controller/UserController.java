package com.social_service.controller;

import com.social_service.constant.Message;
import com.social_service.model.entity.UserEntity;
import com.social_service.model.request.UserRequest;
import com.social_service.model.response.ApiResponse;
import com.social_service.service.UserService;
import com.social_service.util.ApiResponseUtil;
import com.social_service.util.Translator;
import com.social_service.validation.group.OnChangePassword;
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
@RequestMapping("users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Controller")
@SecurityRequirement(name = "bearerToken")
public class UserController {

    UserService userService;

    @Operation(summary = "Tạo mới người dùng", description = "Tạo mới một người dùng vào hệ thống")
    @PostMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> create(@Validated(OnCreate.class) @RequestBody UserRequest request)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                Translator.toLocale(Message.USER_CREATE_SUCCESS.getKey(), null),
                userService.createUser(request)
        );
    }

    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin của một người dùng")
    @PatchMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> update(@Validated(OnUpdate.class) @RequestBody UserRequest request)
            throws Exception {
        userService.updateUser(request);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.NO_CONTENT,
                Translator.toLocale(Message.USER_UPDATE_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Tìm kiếm người dùng", description = "Tìm kiếm và lọc danh sách người dùng")
    @GetMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> search(@Filter Specification<UserEntity> spec, Pageable pageable)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.USER_SEARCH_SUCCESS.getKey(), null),
                userService.searchUsers(spec, pageable)
        );
    }

    @Operation(summary = "Lấy thông tin người dùng", description = "Lấy thông tin chi tiết của một người dùng theo ID")
    @GetMapping(path = "/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> getById(@PathVariable String id) throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.USER_GET_SUCCESS.getKey(), null),
                userService.getUserById(id)
        );
    }

    @Operation(summary = "Khóa người dùng", description = "Khóa tài khoản người dùng")
    @PostMapping(path = "/lock/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> lock(@PathVariable String id) throws Exception {
        userService.lockUser(id);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.NO_CONTENT,
                Translator.toLocale(Message.USER_LOCK_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Mở khóa người dùng", description = "Mở khóa tài khoản người dùng")
    @PostMapping(path = "/unlock/{id}", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> unLock(@PathVariable String id) throws Exception {
        userService.unLockUser(id);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.NO_CONTENT,
                Translator.toLocale(Message.USER_UNLOCK_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Đổi mật khẩu", description = "Đổi mật khẩu tài khoản người dùng")
    @PostMapping(path = "/change-password", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @Validated(OnChangePassword.class) @RequestBody UserRequest request) throws Exception {
        userService.changePassword(request);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.NO_CONTENT,
                Translator.toLocale(Message.PASSWORD_CHANGE_SUCCESS.getKey(), null),
                null
        );
    }
}
