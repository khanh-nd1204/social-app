package com.social_service.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.social_service.constant.Gender;
import com.social_service.util.StringDeserializer;
import com.social_service.validation.constraint.BirthDate;
import com.social_service.validation.group.OnLogin;
import com.social_service.validation.group.OnRegister;
import com.social_service.validation.group.OnResetPassword;
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

    @NotBlank(groups = OnRegister.class, message = "{user_name.required}")
    @Size(min = 5, max = 100, message = "{user_name.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String name;

    @NotBlank(groups = {OnRegister.class, OnResetPassword.class}, message = "email.required}")
    @Email(message = "{email.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String email;

    @NotBlank(groups = OnRegister.class, message = "{phone.required}")
    @Pattern(regexp = "\\d{10}", message = "{phone.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String phone;

    @NotBlank(groups = OnRegister.class, message = "{address.required}")
    @Size(min = 10, max = 100, message = "{address.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String address;

    @NotNull(groups = OnRegister.class, message = "{gender.required}")
    Gender gender;

    @NotNull(groups = OnRegister.class, message = "{birth_date.required}")
    @BirthDate(message = "{birth_date.invalid}")
    LocalDate birthDate;


    @NotNull(groups = OnResetPassword.class, message = "{otp.required}")
    @Min(value = 100000, message = "{otp.length}")
    @Max(value = 999999, message = "{otp.length}")
    Integer otp;
}
