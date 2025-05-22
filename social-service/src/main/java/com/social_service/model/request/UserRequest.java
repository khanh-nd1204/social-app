package com.social_service.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.social_service.constant.Gender;
import com.social_service.util.StringDeserializer;
import com.social_service.validation.constraint.BirthDate;
import com.social_service.validation.group.OnChangePassword;
import com.social_service.validation.group.OnCreate;
import com.social_service.validation.group.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    @NotBlank(groups = {OnUpdate.class, OnChangePassword.class}, message = "{id.required}")
    String id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "{user_name.required}")
    @Size(min = 6, max = 100, message = "{user_name.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String name;

    @NotBlank(groups = OnCreate.class, message = "email.required}")
    @Email(message = "{email.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String email;

    @NotBlank(groups = OnCreate.class, message = "{password.required}")
    @Size(min = 6, max = 32, message = "{password.length}")
    String password;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "{address.required}")
    @Size(min = 10, max = 100, message = "{address.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String address;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "{phone.required}")
    @Pattern(regexp = "\\d{10}", message = "{phone.invalid}")
    @JsonDeserialize(using = StringDeserializer.class)
    String phone;

    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "{gender.required}")
    Gender gender;

    @NotBlank(groups = {OnUpdate.class}, message = "{bio.required}")
    @Size(min = 10, max = 1000, message = "{bio.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String bio;

    @NotBlank(groups = {OnUpdate.class}, message = "{avatar.required}")
    @JsonDeserialize(using = StringDeserializer.class)
    String avatar;

    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "{birth_date.required}")
    @BirthDate(message = "{birth_date.invalid}")
    LocalDate birthDate;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "{role.required")
    Integer roleId;

    @NotBlank(groups = OnChangePassword.class, message = "{new_password.required}")
    @Size(min = 6, max = 32, message = "{password.length}")
    String currentPassword;

    @NotBlank(groups = OnChangePassword.class, message = "{new_password.required}")
    @Size(min = 6, max = 32, message = "{password.length}")
    String newPassword;

    @NotBlank(groups = OnChangePassword.class, message = "{confirm_password.required}")
    @Size(min = 6, max = 32, message = "{confirm_password.length}")
    String confirmPassword;
}
