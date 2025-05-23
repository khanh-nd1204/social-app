package com.social_service.util;

import com.nimbusds.jose.util.Base64;
import com.social_service.constant.TokenType;
import com.social_service.model.entity.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "SECURITY-UTIL")
public class SecurityUtil {

    JwtEncoder jwtEncoder;

    @Value("${jwt.secret-key}")
    @NonFinal
    String secretKey;

    @Value("${jwt.access-token-duration}")
    @NonFinal
    Long accessTokenDuration;

    @Value("${jwt.refresh-token-duration}")
    @NonFinal
    Long refreshTokenDuration;

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public static Optional<String> getCurrentUserJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            return Optional.ofNullable(
                    ((JwtAuthenticationToken) authentication).getToken().getTokenValue());
        }

        return Optional.empty();
    }

    public static Optional<String> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            Object userIdClaim = jwt.getClaim("userId");
            if (userIdClaim != null) {
                return Optional.of(userIdClaim.toString());
            }
        }
        return Optional.empty();
    }

    public String createAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenDuration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .issuer("server")
                .subject(user.getEmail())
                .id(UUID.randomUUID().toString())
                .claim("role", user.getRole().getName())
                .claim("userId", user.getId())
                .claim("type", TokenType.ACCESS_TOKEN)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String createRefreshToken() {
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenDuration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .issuer("server")
                .id(UUID.randomUUID().toString())
                .claim("type", TokenType.REFRESH_TOKEN)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(secretKey).decode();
        return new SecretKeySpec(keyBytes, MacAlgorithm.HS256.getName());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(MacAlgorithm.HS256).build();
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            log.error("JWT decoding failed: {}", e.getMessage());
            throw e;
        }
    }
}
