package com.social_service.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.social_service.constant.EmailType;
import com.social_service.util.StringDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    String subject;
    String template;
    Instant duration;
    Integer otp;

    @NotBlank(message = "{recipient.required}")
    @Email(message = "{recipient.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String recipient;

    @NotNull(message = "{email_type.required}")
    EmailType type;
}
