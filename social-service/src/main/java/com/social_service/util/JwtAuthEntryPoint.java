package com.social_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_service.constant.Message;
import com.social_service.model.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    ObjectMapper mapper;

    MessageSource messageSource;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);

        if (response.isCommitted()) {
            return;
        }

        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Object> res = ApiResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(Translator.toLocale(Message.INVALID_TOKEN.getKey(), null))
                .error(Translator.toLocale(Message.UNAUTHORIZED.getKey(), null))
                .build();

        mapper.writeValue(response.getWriter(), res);
    }
}
