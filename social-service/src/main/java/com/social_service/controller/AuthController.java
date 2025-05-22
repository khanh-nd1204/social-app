package com.social_service.controller;

import com.social_service.constant.Message;
import com.social_service.model.request.AuthRequest;
import com.social_service.model.request.EmailRequest;
import com.social_service.model.response.ApiResponse;
import com.social_service.model.response.AuthResponse;
import com.social_service.service.AuthService;
import com.social_service.util.ApiResponseUtil;
import com.social_service.util.Translator;
import com.social_service.validation.group.OnLogin;
import com.social_service.validation.group.OnRegister;
import com.social_service.validation.group.OnResetPassword;
import com.social_service.validation.group.OnVerify;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@Tag(name = "Auth Controller")
public class AuthController {

    AuthService authService;

    @Operation(summary = "Đăng nhập", description = "Trả về token sau khi đăng nhập thành công")
    @PostMapping(path = "/public/login", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> login(@Validated(OnLogin.class) @RequestBody AuthRequest request)
            throws Exception {
        AuthResponse authResponse = authService.login(request);

        ApiResponse<Object> res =
                ApiResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message(Translator.toLocale(Message.LOGIN_SUCCESS.getKey(), null))
                        .build();

        ResponseCookie springCookie = authService.buildRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).body(res);
    }

    @Operation(summary = "Đăng ký tài khoản", description = "Trả về thông tin tài khoản đăng ký thành công")
    @PostMapping(path = "/public/register", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> register(
            @Validated(OnRegister.class) @RequestBody AuthRequest request) throws Exception {
        authService.register(request);

        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.CREATED,
                Translator.toLocale(Message.REGISTER_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Làm mới token", description = "Trả về access token và refresh token mới")
    @PostMapping(path = "/public/refresh", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@CookieValue(name = "refreshToken") String refreshToken)
            throws Exception {
        AuthResponse authResponse = authService.refreshToken(refreshToken);

        ApiResponse<Object> res =
                ApiResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message(Translator.toLocale(Message.REFRESH_SUCCESS.getKey(), null))
                        .build();

        ResponseCookie springCookie =
                authService.buildRefreshTokenCookie(authResponse.getRefreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).body(res);
    }

    @Operation(
            summary = "Đăng xuất",
            description = "Đăng xuất tài khoản khỏi hệ thống",
            security = @SecurityRequirement(name = "bearerToken")
    )
    @PostMapping(path = "/public/logout", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> logout() throws Exception {
        authService.logout();

        ApiResponse<Object> res =
                ApiResponse.builder()
                        .code(HttpStatus.OK.value())
                        .message(Translator.toLocale(Message.LOGOUT_SUCCESS.getKey(), null))
                        .build();

        ResponseCookie springCookie = authService.buildRefreshTokenCookie("");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).body(res);
    }

    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu cho tài khoản")
    @PostMapping(path = "/public/reset-password", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @Validated(OnResetPassword.class) @RequestBody AuthRequest request) throws Exception {
        authService.resetPassword(request);

        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.PASSWORD_RESET_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Đăng nhập tài khoàn goggle", description = "Trả về url đăng nhập google")
    @GetMapping(path = "/public/google", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> getGoogleUrl() throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.OPERATION_SUCCESS.getKey(), null),
                authService.buildGoogleLoginUrl()
        );
    }

    @Operation(
            summary = "Lấy thông tin tài khoản google",
            description = "Trả về thông tin tài khoản sau khi đăng nhập google thành công"
    )
    @GetMapping(path = "/public/google/call-back", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> handleCallback(@RequestParam("code") String code) throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.LOGIN_SUCCESS.getKey(), null),
                authService.loginGoogleCallBack(code)
        );
    }

    @Operation(summary = "Gửi mã OTP", description = "Gửi mã OTP về mail người dùng")
    @PostMapping(path = "/public/otp", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> sendOTP(@RequestBody EmailRequest request) throws Exception {
        authService.sendOTP(request);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.EMAIL_SEND_SUCCESS.getKey(), null),
                null
        );
    }

    @Operation(summary = "Xác thực email", description = "Xác thực email người dùng qua mã OTP")
    @PostMapping(path = "/public/verify", headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@Validated(OnVerify.class) @RequestBody AuthRequest request)
            throws Exception {
        authService.verify(request);
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.USER_VERIFIED_SUCCESS.getKey(), null),
                null
        );
    }
}
