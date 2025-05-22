package com.social_service.model.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.social_service.util.StringDeserializer;
import com.social_service.validation.group.OnCreate;
import com.social_service.validation.group.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @NotNull(groups = OnUpdate.class, message = "{id.required}")
    Integer id;

    @NotBlank(groups = {OnCreate.class}, message = "{role_name.required}")
    @Size(min = 6, max = 100, message = "{role_name.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String name;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "{sub_description.required}")
    @Size(min = 10, max = 100, message = "{sub_description.length}")
    @JsonDeserialize(using = StringDeserializer.class)
    String description;

    @NotEmpty(groups = {OnCreate.class, OnUpdate.class}, message = "{permissions.required}")
    List<Integer> permissionIds;
}
