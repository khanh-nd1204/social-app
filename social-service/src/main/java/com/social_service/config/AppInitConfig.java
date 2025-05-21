package com.social_service.config;

import com.social_service.constant.Role;
import com.social_service.constant.User;
import com.social_service.model.entity.PermissionEntity;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.entity.UserEntity;
import com.social_service.repository.PermissionRepository;
import com.social_service.repository.RoleRepository;
import com.social_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@Slf4j(topic = "APP-CONFIG")
public class AppInitConfig {

    @Bean
    ApplicationRunner init(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.findByName(Role.ADMIN.getName()).isEmpty()) {
                RoleEntity role = RoleEntity.builder()
                        .name(Role.ADMIN.getName())
                        .description(Role.ADMIN.getDescription())
                        .build();

                List<PermissionEntity> permissions = permissionRepository.findAll();
                role.setPermissions(permissions);
                roleRepository.save(role);
                log.info("Init role success: {}", role.getName());
            }

            if (userRepository.findByEmail(User.ADMIN.getEmail()).isEmpty()) {
                UserEntity user = UserEntity.builder()
                        .email(User.ADMIN.getEmail())
                        .password(passwordEncoder.encode(User.ADMIN.getPassword()))
                        .name(User.ADMIN.getName())
                        .verified(true)
                        .active(true)
                        .build();

                RoleEntity role = roleRepository.findByName(Role.ADMIN.getName()).orElse(null);
                user.setRole(role);
                userRepository.save(user);
                log.info("Init user success: {}", user.getEmail());
            }
        };
    }
}
