package com.social_service.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemLogResponse {
    Integer id;
    String action;
    String description;
    String params;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy", timezone = "GMT+7")
    Instant createdAt;
}
