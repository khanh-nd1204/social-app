package com.social_service.service;

import com.social_service.model.request.AuthRequest;
import com.social_service.model.response.AuthResponse;
import org.springframework.http.ResponseCookie;

public interface AuthService {

    AuthResponse login(AuthRequest request) throws Exception;

    void register(AuthRequest request) throws Exception;

    void resetPassword(AuthRequest request) throws Exception;

    AuthResponse refreshToken(String refreshToken) throws Exception;

    void logout() throws Exception;

    String buildGoogleLoginUrl() throws Exception;

    AuthResponse loginGoogleCallBack(String code) throws Exception;

    ResponseCookie buildRefreshTokenCookie(String refreshToken) throws Exception;
}
