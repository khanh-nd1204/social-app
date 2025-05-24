package com.social_service.service.impl;

import com.social_service.constant.EmailStatus;
import com.social_service.constant.EmailType;
import com.social_service.constant.Message;
import com.social_service.constant.Role;
import com.social_service.exception.NotFoundException;
import com.social_service.exception.UnauthorizedException;
import com.social_service.model.entity.InvalidatedTokenEntity;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.entity.UserEntity;
import com.social_service.model.request.AuthRequest;
import com.social_service.model.request.EmailRequest;
import com.social_service.model.response.AuthResponse;
import com.social_service.repository.*;
import com.social_service.service.AuthService;
import com.social_service.service.EmailService;
import com.social_service.service.SystemLogService;
import com.social_service.service.UserService;
import com.social_service.util.SecurityUtil;
import com.social_service.util.Translator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "AUTH-SERVICE")
public class AuthServiceImpl implements AuthService {

    AuthenticationManagerBuilder authenticationManagerBuilder;

    SecurityUtil securityUtil;

    UserService userService;

    InvalidatedTokenRepository invalidatedTokenRepository;

    JwtDecoder jwtDecoder;

    SystemLogService systemLogService;

    RestTemplate restTemplate = new RestTemplate();

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    EmailService emailService;

    EmailRepository emailRepository;

    PermissionRepository permissionRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    @NonFinal
    String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    @NonFinal
    String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    @NonFinal
    String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    @NonFinal
    String googleTokenUri;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    @NonFinal
    String googleAuthorizationUri;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    @NonFinal
    String googleUserInfoUri;

    @Value("${spring.mail.duration}")
    @NonFinal
    int emailDuration;

    @Override
    public AuthResponse login(AuthRequest request) throws Exception {
        log.info("Login request: {}", request.toString());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        log.info("Authentication: {}", authentication.toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userService.getUserByEmail(request.getUsername());

        checkUserValid(user);

        String refreshToken = securityUtil.createRefreshToken();
        userService.updateRefreshToken(user.getId(), refreshToken);

        systemLogService.createLog(null, Message.LOGIN.getKey(), Message.LOGIN_SUCCESS.getKey());

        return AuthResponse.builder()
                .accessToken(securityUtil.createAccessToken(user))
                .refreshToken(refreshToken)
                .authorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void register(AuthRequest request) throws Exception {
        log.info("Register request: {}", request.toString());

        UserEntity userByEmail = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (userByEmail != null) {
            checkUserValid(userByEmail);
            throw new BadRequestException(Translator.toLocale(Message.EMAIL_EXISTS.getKey(), null));
        }

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .password(passwordEncoder.encode(request.getPassword()))
                .verified(false)
                .active(false)
                .build();

        RoleEntity role = roleRepository.findByName(Role.USER.getName()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null)));
        user.setRole(role);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Translator.toLocale(Message.USER_EXISTS.getKey(), null));
        }

        generateOTPAndEmail(user, EmailType.REGISTER);
    }

    @Override
    @Transactional
    public void verify(AuthRequest request) throws Exception {
        log.info("Verify request: {}", request.toString());

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null))
        );

        if (user.getVerified()) {
            throw new BadRequestException(Translator.toLocale(Message.ACCOUNT_VERIFIED.getKey(), null));
        }

//        if (!user.getActive()) {
//            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_LOCKED.getKey(), null));
//        }

        verifyOtp(user, request.getOtp());

        user.setVerified(true);
        user.setActive(true);
        userRepository.save(user);

        systemLogService.createLog(null, Message.VERIFY.getKey(), Message.ACCOUNT_VERIFIED_SUCCESS.getKey());
    }

    @Override
    @Transactional
    public void sendOTP(EmailRequest request) throws Exception {
        log.info("Send OTP email request: {}", request.toString());

        UserEntity user = userRepository.findByEmail(request.getRecipient()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null))
        );

        if (user.getVerified() && request.getType().equals(EmailType.VERIFY_EMAIL)) {
            throw new BadRequestException(Translator.toLocale(Message.ACCOUNT_VERIFIED.getKey(), null));
        }

        if (!user.getVerified() && request.getType().equals(EmailType.RESET_PASSWORD)) {
            throw new BadRequestException(Translator.toLocale(Message.ACCOUNT_UNVERIFIED.getKey(), null));
        }

        if (!user.getActive() && user.getVerified()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_LOCKED.getKey(), null));
        }

        generateOTPAndEmail(user, request.getType());
    }

    @Override
    @Transactional
    public void resetPassword(AuthRequest request) throws Exception {
        log.info("Reset password request: {}", request.toString());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(Translator.toLocale(Message.PASSWORD_NOT_MATCHED.getKey(), null));
        }

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null)));

        checkUserValid(user);

        verifyOtp(user, request.getOtp());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        systemLogService.createLog(null, Message.PASSWORD_RESET.getKey(), Message.PASSWORD_CHANGE_SUCCESS.getKey());
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) throws Exception {
        log.info("Refresh token request: {}", refreshToken);

        if (refreshToken == null) {
            throw new UnauthorizedException(Translator.toLocale(Message.INVALID_TOKEN.getKey(), null));
        }

        Jwt decodedToken = securityUtil.checkValidRefreshToken(refreshToken);
        log.info("Decode refresh token: {}", decodedToken.toString());

        UserEntity user = userService.getUserByRefreshToken(refreshToken);

        checkUserValid(user);

        String newRefreshToken = securityUtil.createRefreshToken();
        userService.updateRefreshToken(user.getId(), newRefreshToken);

        return AuthResponse.builder()
                .accessToken(securityUtil.createAccessToken(user))
                .refreshToken(newRefreshToken)
                .authorities(securityUtil.getAuthorities(user))
                .build();
    }

    @Override
    public void logout() throws Exception {
        log.info("Logout request");

        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() ->
                new UnauthorizedException(Translator.toLocale(Message.INVALID_TOKEN.getKey(), null)));

        log.info("Logout user: {}", email);

        UserEntity user = userService.getUserByEmail(email);
        userService.updateRefreshToken(user.getId(), "");

        String token = SecurityUtil.getCurrentUserJWT().orElseThrow(() ->
                new UnauthorizedException(Translator.toLocale(Message.INVALID_TOKEN.getKey(), null)));

        Jwt jwt = jwtDecoder.decode(token);

        InvalidatedTokenEntity invalidatedToken =
                InvalidatedTokenEntity.builder().id(jwt.getId()).expiredAt(jwt.getExpiresAt()).build();
        invalidatedTokenRepository.save(invalidatedToken);

        systemLogService.createLog(null, Message.LOGOUT.getKey(), Message.LOGOUT_SUCCESS.getKey());
    }

    @Override
    public String buildGoogleLoginUrl() throws Exception {
        log.info("Build google login url");

        return UriComponentsBuilder.fromHttpUrl(googleAuthorizationUri)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")
                .encode()
                .build()
                .toUriString();
    }

    @Override
    @Transactional
    public AuthResponse loginGoogleCallBack(String code) throws Exception {
        log.info("Login callback request: {}", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
        tokenRequest.add("code", code);
        tokenRequest.add("client_id", googleClientId);
        tokenRequest.add("client_secret", googleClientSecret);
        tokenRequest.add("redirect_uri", googleRedirectUri);
        tokenRequest.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(tokenRequest, headers);

        ResponseEntity<Map> tokenResponse =
                restTemplate.postForEntity(googleTokenUri, requestEntity, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<?> userInfoEntity = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfo =
                restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, userInfoEntity, Map.class);

        Map<String, Object> userBody = userInfo.getBody();

        log.info("User body: {}", userBody);

        String email = (String) userBody.get("email");
        String name = (String) userBody.get("name");
        String googleId = (String) userBody.get("sub");
        String avatar = (String) userBody.get("picture");

        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            RoleEntity role = roleRepository.findByName(Role.USER.getName()).orElseThrow(() ->
                    new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null)));

            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .name(name)
                    .password("")
                    .googleId(googleId)
                    .role(role)
                    .verified(true)
                    .active(true)
                    .avatar(avatar)
                    .build();

            userRepository.save(newUser);

            String refreshToken = securityUtil.createRefreshToken();
            userService.updateRefreshToken(newUser.getId(), refreshToken);

            return AuthResponse.builder()
                    .accessToken(securityUtil.createAccessToken(newUser))
                    .refreshToken(refreshToken)
                    .authorities(securityUtil.getAuthorities(newUser))
                    .build();
        } else {
            checkUserValid(user);

            String refreshToken = securityUtil.createRefreshToken();
            userService.updateRefreshToken(user.getId(), refreshToken);

            return AuthResponse.builder()
                    .accessToken(securityUtil.createAccessToken(user))
                    .refreshToken(refreshToken)
                    .authorities(securityUtil.getAuthorities(user))
                    .build();
        }
    }

    @Override
    public ResponseCookie buildRefreshTokenCookie(String refreshToken, Long duration) throws Exception {
        log.info("Build refresh token cookie");

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(duration)
                .build();
    }

    public void checkUserValid(UserEntity user) {
        if (!user.getVerified()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_UNVERIFIED.getKey(), null));
        }

        if (!user.getActive()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_LOCKED.getKey(), null));
        }
    }

    public void generateOTPAndEmail(UserEntity user, EmailType type) throws Exception {
        if (emailRepository.existsByRecipientAndStatusAndDurationIsBefore(
                user.getEmail(), EmailStatus.PENDING, Instant.now())
        ) {
            log.info("Email already in pending status: {}", user.getEmail());
            return;
        }

        log.info("Generate OTP and email");

        int otp = new Random().nextInt(900000) + 100000;
        Instant otpDuration = Instant.now().plusSeconds(emailDuration);

        EmailRequest request = EmailRequest.builder()
                .recipient(user.getEmail())
                .subject("OTP Authentication")
                .template("otp-template")
                .duration(otpDuration)
                .type(type)
                .build();

        log.info("Sending email request: {}", request.toString());

        emailService.saveEmail(request);

        user.setOtp(otp);
        user.setOtpDuration(otpDuration);

        userRepository.save(user);
    }

    public void verifyOtp(UserEntity user, int enteredOtp) throws Exception {
        log.info("Verify OTP: {}, enteredOtp: {}", user.getOtp(), enteredOtp);

        if (user.getOtp() == null || !user.getOtp().equals(enteredOtp)) {
            throw new BadRequestException(Translator.toLocale(Message.OTP_INVALID.getKey(), null));
        }

        if (user.getOtpDuration().isBefore(Instant.now())) {
            throw new BadRequestException(Translator.toLocale(Message.OTP_EXPIRED.getKey(), null));
        }

        user.setOtp(null);
        user.setOtpDuration(null);
        userRepository.save(user);
    }
}
