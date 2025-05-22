package com.social_service.service;

import com.social_service.model.entity.SystemLogEntity;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.SystemLogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface SystemLogService {

    PageResponse<List<SystemLogResponse>> searchLogs(Specification<SystemLogEntity> spec, Pageable pageable)
            throws Exception;

    void createLog(String params, String action, String description) throws Exception;

    void cleanLogs() throws Exception;
}
