package com.social_service.service.impl;

import com.social_service.constant.Message;
import com.social_service.model.entity.UserEntity;
import com.social_service.repository.UserRepository;
import com.social_service.util.SecurityUtil;
import com.social_service.util.Translator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "USER-DETAIL-SERVICE")
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    SecurityUtil securityUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        log.info("Load user by username: {}", username);

        UserEntity user = userRepository.findByEmail(username).orElseThrow(() ->
                new BadCredentialsException(Translator.toLocale(Message.ACCOUNT_INVALID.getKey(), null)));

        if (!user.getVerified()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_UNVERIFIED.getKey(), null));
        }

        if (!user.getActive()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_LOCKED.getKey(), null));
        }

        List<String> authorities = securityUtil.getAuthorities(user);

        List<GrantedAuthority> grantedAuthorities =
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(grantedAuthorities)
                .build();
    }
}
