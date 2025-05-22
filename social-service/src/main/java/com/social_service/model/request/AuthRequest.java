package com.social_service.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.social_service.constant.Gender;
import com.social_service.util.StringDeserializer;
import com.social_service.validation.constraint.BirthDate;
import com.social_service.validation.group.OnLogin;
import com.social_service.validation.group.OnRegister;
import com.social_service.validation.group.OnResetPassword;
import com.social_service.validation.group.OnVerify;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequest {

    @NotBlank(groups = OnLogin.class, message = "{email.required}")
    @Email(message = "{email.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String username;

    @NotBlank(groups = {OnLogin.class, OnRegister.class, OnResetPassword.class}, message = "{password.required}")
    @Size(min = 6, max = 32, message = "{password.length}")
    String password;

    @NotBlank(groups = {OnResetPassword.class}, message = "{confirm_password.required}")
    @Size(min = 6, max = 32, message = "{confirm_password.length}")
    String confirmPassword;

    @NotBlank(groups = OnRegister.class, message = "{user_name.required}")
    @Size(min = 5, max = 100, message = "{user_name.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String name;

    @NotBlank(groups = {OnRegister.class, OnResetPassword.class, OnVerify.class}, message = "email.required}")
    @Email(message = "{email.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String email;

    @NotNull(groups = OnRegister.class, message = "{gender.required}")
    Gender gender;

    @NotNull(groups = OnRegister.class, message = "{birth_date.required}")
    @BirthDate(message = "{birth_date.invalid}")
    LocalDate birthDate;

    @NotNull(groups = {OnResetPassword.class, OnVerify.class}, message = "{otp.required}")
    @Min(value = 100000, message = "{otp.invalid}")
    @Max(value = 999999, message = "{otp.invalid}")
    Integer otp;
}
