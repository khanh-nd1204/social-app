package com.social_service.constant;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN", "Admin role"),
    USER("ROLE_USER", "User role");

    private final String name;
    private final String description;

    Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
