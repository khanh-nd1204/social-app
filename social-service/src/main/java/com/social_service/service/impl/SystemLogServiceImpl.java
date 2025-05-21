package com.social_service.service.impl;

import com.social_service.constant.Message;
import com.social_service.model.entity.SystemLogEntity;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.SystemLogResponse;
import com.social_service.repository.SystemLogRepository;
import com.social_service.service.SystemLogService;
import com.social_service.util.Translator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "LOG-SERVICE")
public class SystemLogServiceImpl implements SystemLogService {

    SystemLogRepository systemLogRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<List<SystemLogResponse>> searchLog(Specification<SystemLogEntity> spec, Pageable pageable) throws Exception {
        log.info("Searching log: {}", spec.toString());

        Page<SystemLogEntity> pageData = systemLogRepository.findAll(spec, pageable);

        List<SystemLogResponse> systemLogResponses = pageData.getContent().stream().map(log -> {
            String translatedDesc = Translator.toLocale(log.getDescription(), new Object[]{log.getParams()});
            String translatedAction = Translator.toLocale(log.getAction(), new Object[]{log.getParams()});

            return SystemLogResponse.builder()
                    .id(log.getId())
                    .action(translatedAction)
                    .description(translatedDesc)
                    .createdAt(log.getCreatedAt())
                    .build();
        }).toList();

        return PageResponse.<List<SystemLogResponse>>builder()
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .result(systemLogResponses)
                .build();
    }

    @Override
    public void createLog(String params, String action, String description) throws Exception {
        SystemLogEntity log = SystemLogEntity.builder()
                .action(action)
                .description(description)
                .params(params)
                .build();

        systemLogRepository.save(log);
    }

    @Override
    public void clearLogs() throws Exception {
        Instant instant = Instant.now().minus(15, ChronoUnit.DAYS);
        systemLogRepository.deleteByCreatedAtBefore(instant);
        createLog(null, Message.CLEAN.getKey(), Message.CLEAN_LOG.getKey());
    }
}
