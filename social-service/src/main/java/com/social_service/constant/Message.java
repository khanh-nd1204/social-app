package com.social_service.constant;

import lombok.Getter;

@Getter
public enum Message {
    LOGIN_SUCCESS("login.success"),
    INVALID_TOKEN("token.invalid"),
    UNAUTHORIZED("unauthorized"),
    SERVER_ERROR("server.error"),
    ERROR("error"),
    NOT_FOUND("not_found"),
    BAD_REQUEST("bad_request"),
    FORBIDDEN("forbidden"),
    INVALID_PARAMETER("parameter.invalid"),
    MISSING_PARAMETER("parameter.missing"),
    UPLOAD_ERROR("upload.error"),
    FILE_EXCEEDED("file.exceeded"),
    ACCOUNT_LOCK("account.lock"),
    ACCOUNT_UNVERIFIED("account.unverified"),
    LOGIN("login"),
    USER_LOGIN("user.login"),
    LOGOUT("logout"),
    USER_LOGOUT("user.logout"),
    ROLE_NOT_FOUND("role.not_found"),
    EMAIL_EXISTS("email.exists"),
    PHONE_EXISTS("phone.exists"),
    USER_EXISTS("user.exists"),
    USER_NOT_FOUND("user.not_found"),
    INVALID_OTP("otp.invalid"),
    OTP_EXPIRED("otp.expired"),
    RESET_PASSWORD("password.reset"),
    USER_RESET_PASSWORD("user_password.reset"),
    FETCH_SUCCESS("fetch.success"),
    REGISTER_SUCCESS("register.success"),
    REFRESH_SUCCESS("refresh.success"),
    LOGOUT_SUCCESS("logout.success"),
    SUCCESS("success"),
    CREATE("create"),
    USER_CREATE("user.create"),
    UPDATE("update"),
    USER_UPDATE("user.update"),
    DELETE("delete"),
    PASSWORD_INCORRECT("password.incorrect"),
    PASSWORD_NOT_MATCHED("password.not_matched"),
    CHANGE_PASSWORD("change.password"),
    USER_CHANGE_PASSWORD("user_password.change"),
    USER_LOCKED("user.locked"),
    LOCK("lock"),
    USER_UNLOCKED("user.unlocked"),
    UNLOCK("unlock"),
    ACCOUNT_INVALID("account.invalid"),
    ROLE_NAME_EXISTS("role_name.exists"),
    ROLE_EXISTS("role.exists"),
    ROLE_CREATE("role.create"),
    ROLE_UPDATE("role.update"),
    DELETE_FAIL("delete.fail"),
    ROLE_DELETE("role.delete"),
    PERMISSION_NAME_EXISTS("permission_name.exists"),
    PERMISSION_EXISTS("permission.exists"),
    PERMISSION_CREATE("permission.create"),
    PERMISSION_NOT_FOUND("permission.not_found"),
    PERMISSION_UPDATE("permission.update"),
    PERMISSION_DELETE("permission.delete"),
    CLEAN("clean"),
    CLEAN_LOG("clean.log"),
    ;

    private final String key;

    Message(String key) {
        this.key = key;
    }
}
