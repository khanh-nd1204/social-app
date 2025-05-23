package com.social_service.util;

import com.social_service.constant.Message;
import com.social_service.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "CUSTOM-JWT-DECODER")
public class CustomJwtDecoder implements JwtDecoder {

    JwtDecoder jwtDecoder;

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public Jwt decode(String token) throws JwtException {
        log.debug("Decoding token: {}", token);

        Jwt jwt = jwtDecoder.decode(token);

        String tokenId = jwt.getId();

        if (invalidatedTokenRepository.findById(tokenId).isPresent()) {
            throw new JwtException(Translator.toLocale(Message.INVALID_TOKEN.getKey(), null));
        }

        return jwt;
    }
}
