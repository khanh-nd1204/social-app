package com.social_service.controller;

import com.social_service.constant.Message;
import com.social_service.model.entity.SystemLogEntity;
import com.social_service.model.response.ApiResponse;
import com.social_service.service.SystemLogService;
import com.social_service.util.ApiResponseUtil;
import com.social_service.util.Translator;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("logs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "System Controller")
@SecurityRequirement(name = "bearerToken")
public class SystemController {

    SystemLogService systemLogService;

    @Operation(summary = "Tìm kiếm nhật ký", description = "Tìm kiếm và lọc danh sách nhật ký")
    @GetMapping(headers = "apiVersion=v1.0")
    public ResponseEntity<ApiResponse<Object>> search(@Filter Specification<SystemLogEntity> spec, Pageable pageable)
            throws Exception {
        return ApiResponseUtil.buildSuccessResponse(
                HttpStatus.OK,
                Translator.toLocale(Message.LOG_SEARCH_SUCCESS.getKey(), null),
                systemLogService.searchLogs(spec, pageable)
        );
    }
}
