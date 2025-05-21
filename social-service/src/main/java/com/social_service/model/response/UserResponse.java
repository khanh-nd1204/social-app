package com.social_service.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.social_service.constant.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String name;
    String email;
    String address;
    String phone;
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "GMT+7")
    LocalDate birthDate;
    Boolean verified;
    Boolean active;
    Gender gender;
    String bio;
    String avatar;
    String googleId;
    String roleName;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy", timezone = "GMT+7")
    Instant createdAt;
    String createdBy;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy", timezone = "GMT+7")
    Instant updatedAt;
    String updatedBy;
}
