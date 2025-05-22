package com.social_service.constant;

import lombok.Getter;

@Getter
public enum User {
    ADMIN("admin@test.com", "123456", "Admin"),
    USER("user@test.com", "123456", "User");

    private final String email;
    private final String password;
    private final String name;

    User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
