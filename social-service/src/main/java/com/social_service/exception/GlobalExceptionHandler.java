package com.social_service.exception;

import com.social_service.constant.Message;
import com.social_service.model.response.ApiResponse;
import com.social_service.util.ApiResponseUtil;
import com.social_service.util.Translator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    // Xử lý lỗi tổng quát (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException() {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Translator.toLocale(Message.ERROR.getKey(), null),
                Translator.toLocale(Message.SERVER_ERROR.getKey(), null)
        );
    }

    // Xử lý lỗi không tìm thấy tài nguyên (404)
    @ExceptionHandler({NoResourceFoundException.class, NotFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(Exception e) {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.NOT_FOUND,
                e.getMessage(),
                Translator.toLocale(Message.NOT_FOUND.getKey(), null)
        );
    }

    // Xử lý lỗi yêu cầu không hợp lệ (400)
    @ExceptionHandler({
            BadRequestException.class,
            MissingRequestCookieException.class,
            IllegalArgumentException.class,
            HttpRequestMethodNotSupportedException.class,
            DataIntegrityViolationException.class,
            PropertyReferenceException.class,
            InvalidDataAccessApiUsageException.class,
            NullPointerException.class,
            HttpMessageNotReadableException.class,
            DisabledException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(Exception e) {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Translator.toLocale(Message.BAD_REQUEST.getKey(), null)
        );
    }

    // Xử lý lỗi xác thực (401)
    @ExceptionHandler({
            UnauthorizedException.class,
            BadJwtException.class,
            AuthenticationException.class,
            InternalAuthenticationServiceException.class,
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(Exception e) {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                e.getMessage(),
                Translator.toLocale(Message.UNAUTHORIZED.getKey(), null)
        );
    }

    // Xử lý lỗi truy cập bị cấm (403)
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(Exception e) {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.FORBIDDEN,
                e.getMessage(),
                Translator.toLocale(Message.FORBIDDEN.getKey(), null)
        );
    }

    // Xử lý lỗi kiểu dữ liệu không đúng trong request (400)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException() {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Translator.toLocale(Message.INVALID_PARAMETER.getKey(), null),
                Translator.toLocale(Message.BAD_REQUEST.getKey(), null)
        );
    }

    // Xử lý lỗi thiếu tham số request (400)
    @ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiResponse<Object>> handleMissingParameterException() {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Translator.toLocale(Message.MISSING_PARAMETER.getKey(), null),
                Translator.toLocale(Message.BAD_REQUEST.getKey(), null)
        );
    }

    // Xử lý lỗi tham số không hợp lệ trong request body #1 (400)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors =
                e.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();

        Object errorDetails = errors.size() == 1 ? errors.get(0) : errors;

        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorDetails,
                Translator.toLocale(Message.BAD_REQUEST.getKey(), null)
        );
    }

    // Xử lý lỗi tham số không hợp lệ trong request body #2 (400)
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errors =
                e.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage).toList();

        Object errorDetails = errors.size() == 1 ? errors.get(0) : errors;

        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorDetails,
                Translator.toLocale(Message.BAD_REQUEST.getKey(), null)
        );
    }

    // Xử lý lỗi tải tập tin (400)
    @ExceptionHandler({FileStoreException.class, IOException.class, MultipartException.class})
    public ResponseEntity<ApiResponse<Object>> handleFileException(Exception e) {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                Translator.toLocale(Message.UPLOAD_ERROR.getKey(), null)
        );
    }

    // Xử lý lỗi kích thước tập tin (400)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException() {
        return ApiResponseUtil.buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                Translator.toLocale(Message.FILE_EXCEEDED.getKey(), null),
                Translator.toLocale(Message.UPLOAD_ERROR.getKey(), null)
        );
    }
}
