package com.social_service.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {
    Integer id;
    String name;
    String apiPath;
    String method;
    String description;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy", timezone = "GMT+7")
    Instant createdAt;
    String createdBy;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy", timezone = "GMT+7")
    Instant updatedAt;
    String updatedBy;
}
