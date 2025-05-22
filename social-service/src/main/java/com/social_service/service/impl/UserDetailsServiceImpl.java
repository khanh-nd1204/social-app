package com.social_service.service.impl;

import com.social_service.constant.Message;
import com.social_service.model.entity.UserEntity;
import com.social_service.repository.UserRepository;
import com.social_service.util.Translator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(() ->
                new BadCredentialsException(Translator.toLocale(Message.ACCOUNT_INVALID.getKey(), null)));

        if (!user.getActive()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_LOCKED.getKey(), null));
        }

        if (!user.getVerified()) {
            throw new DisabledException(Translator.toLocale(Message.ACCOUNT_UNVERIFIED.getKey(), null));
        }

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName())))
                .build();
    }
}
