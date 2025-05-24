package com.social_service.constant;

import lombok.Getter;

@Getter
public enum Message {
    // === AUTH ===
    LOGIN_SUCCESS("auth.login.success"),
    UNAUTHORIZED("auth.unauthorized"),
    INVALID_TOKEN("auth.token.invalid"),
    FORBIDDEN("auth.forbidden"),
    LOGOUT_SUCCESS("auth.logout.success"),
    REFRESH_SUCCESS("auth.refresh.success"),
    REGISTER_SUCCESS("auth.register.success"),
    ACCESS_DENIED("auth.access.denied"),

    // === USER ===
    USER_CREATE_SUCCESS("user.create.success"),
    USER_UPDATE_SUCCESS("user.update.success"),
    USER_SEARCH_SUCCESS("user.search.success"),
    USER_GET_SUCCESS("user.get.success"),
    USER_EXISTS("user.exists"),
    USER_NOT_FOUND("user.not_found"),
    EMAIL_EXISTS("user.email.exists"),
    EMAIL_INVALID("user.email.invalid"),
    PHONE_EXISTS("user.phone.exists"),
    USER_LOCK_SUCCESS("user.lock.success"),
    USER_LOCK_FAILED("user.lock.fail"),
    USER_UNLOCK_SUCCESS("user.unlock.success"),

    // === PASSWORD ===
    PASSWORD_INCORRECT("password.incorrect"),
    PASSWORD_NOT_MATCHED("password.not_matched"),
    PASSWORD_RESET_SUCCESS("password.reset.success"),
    PASSWORD_CHANGE_SUCCESS("password.change.success"),

    // === ROLE ===
    ROLE_CREATE_SUCCESS("role.create.success"),
    ROLE_UPDATE_SUCCESS("role.update.success"),
    ROLE_DELETE_SUCCESS("role.delete.success"),
    ROLE_DELETE_FAIL("role.delete.fail"),
    ROLE_SEARCH_SUCCESS("role.search.success"),
    ROLE_GET_SUCCESS("role.get.success"),
    ROLE_EXISTS("role.exists"),
    ROLE_NOT_FOUND("role.not_found"),
    ROLE_NAME_EXISTS("role.name.exists"),

    // === PERMISSION ===
    PERMISSION_CREATE_SUCCESS("permission.create.success"),
    PERMISSION_UPDATE_SUCCESS("permission.update.success"),
    PERMISSION_DELETE_SUCCESS("permission.delete.success"),
    PERMISSION_SEARCH_SUCCESS("permission.search.success"),
    PERMISSION_GET_SUCCESS("permission.get.success"),
    PERMISSION_EXISTS("permission.exists"),
    PERMISSION_NOT_FOUND("permission.not_found"),
    PERMISSION_NAME_EXISTS("permission.name.exists"),

    // === FILE ===
    UPLOAD_ERROR("file.upload.error"),
    FILE_EXCEEDED("file.upload.exceeded"),

    // === OTP ===
    OTP_INVALID("otp.invalid"),
    OTP_EXPIRED("otp.expired"),
    OTP_SEND_SUCCESS("otp.send.success"),

    // === ACCOUNT ===
    ACCOUNT_LOCKED("account.locked"),
    ACCOUNT_UNVERIFIED("account.unverified"),
    ACCOUNT_INVALID("account.invalid"),
    ACCOUNT_ACTIVE("account.active"),
    ACCOUNT_VERIFIED("account.verified"),
    ACCOUNT_VERIFIED_SUCCESS("account.verified.success"),

    // === SYSTEM / COMMON ===
    ERROR("error"),
    SERVER_ERROR("system.error"),
    BAD_REQUEST("request.bad"),
    NOT_FOUND("resource.not_found"),
    INVALID_PARAMETER("request.parameter.invalid"),
    MISSING_PARAMETER("request.parameter.missing"),
    OPERATION_SUCCESS("operation.success"),
    CREATE("operation.create"),
    UPDATE("operation.update"),
    DELETE("operation.delete"),
    LOCK("operation.lock"),
    UNLOCK("operation.unlock"),
    LOGIN("operation.login"),
    LOGOUT("operation.logout"),
    PASSWORD_RESET("operation.password.reset"),
    PASSWORD_CHANGE("operation.password.change"),
    CLEAN("operation.clean"),
    CLEAN_SUCCESS("clean.success"),
    VERIFY("operation.verify"),
    LOG_SEARCH_SUCCESS("log.search.success"),
    ;

    private final String key;

    Message(String key) {
        this.key = key;
    }
}
