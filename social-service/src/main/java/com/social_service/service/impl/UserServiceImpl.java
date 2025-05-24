package com.social_service.service.impl;

import com.social_service.constant.Message;
import com.social_service.constant.Role;
import com.social_service.constant.User;
import com.social_service.exception.NotFoundException;
import com.social_service.mapper.UserMapper;
import com.social_service.model.entity.RoleEntity;
import com.social_service.model.entity.UserEntity;
import com.social_service.model.request.UserRequest;
import com.social_service.model.response.PageResponse;
import com.social_service.model.response.UserResponse;
import com.social_service.repository.RoleRepository;
import com.social_service.repository.UserRepository;
import com.social_service.service.SystemLogService;
import com.social_service.service.UserService;
import com.social_service.util.SecurityUtil;
import com.social_service.util.Translator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    RoleRepository roleRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    SystemLogService systemLogService;

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('CREATE_USER', 'ROLE_ADMIN')")
    public UserResponse createUser(UserRequest request) throws Exception {
        log.info("Creating user {}", request.toString());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(Translator.toLocale(Message.EMAIL_EXISTS.getKey(), null));
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException(Translator.toLocale(Message.PHONE_EXISTS.getKey(), null));
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        RoleEntity role = roleRepository.findById(request.getRoleId()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null)));
        user.setRole(role);

        user.setActive(true);
        user.setVerified(true);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(Translator.toLocale(Message.USER_EXISTS.getKey(), null));
        }

        systemLogService.createLog(user.getId(), Message.CREATE.getKey(), Message.USER_CREATE_SUCCESS.getKey());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('UPDATE_USER', 'ROLE_ADMIN')")
    public void updateUser(UserRequest request) throws Exception {
        log.info("Updating user {}", request.toString());

        checkCurrentUser(request.getId());

        UserEntity user = userRepository.findById(request.getId()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null)));

        UserEntity userByPhone = userRepository.findByPhone(request.getPhone()).orElse(null);
        if (userByPhone != null && !userByPhone.getId().equals(user.getId())) {
            throw new BadRequestException(Translator.toLocale(Message.PHONE_EXISTS.getKey(), null));
        }

        userMapper.updateEntity(user, request);

        RoleEntity role = roleRepository.findById(request.getRoleId()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.ROLE_NOT_FOUND.getKey(), null)));
        user.setRole(role);

        userRepository.save(user);

        systemLogService.createLog(user.getId(), Message.UPDATE.getKey(), Message.USER_UPDATE_SUCCESS.getKey());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('GET_USER', 'ROLE_ADMIN')")
    public UserResponse getUserById(String id) throws Exception {
        log.info("Retrieving user by id {}", id);

        checkCurrentUser(id);

        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null))
        );

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('SEARCH_USERS', 'ROLE_ADMIN')")
    public PageResponse<List<UserResponse>> searchUsers(Specification<UserEntity> spec, Pageable pageable) throws Exception {
        log.info("Searching user {}", spec.toString());

        Specification<UserEntity> users =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.isTrue(root.get("verified"));

        Page<UserEntity> pageData = userRepository.findAll(spec.and(users), pageable);

        return PageResponse.<List<UserResponse>>builder()
                .page(pageable.getPageNumber() + 1)
                .size(pageable.getPageSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .result(pageData.getContent().stream().map(userMapper::toResponse).toList())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('LOCK_USER', 'ROLE_ADMIN')")
    public void lockUser(String id) throws Exception {
        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null)));

        if (!user.getActive()) {
            throw new BadRequestException(Translator.toLocale(Message.ACCOUNT_LOCKED.getKey(), null));
        }

        if (user.getEmail().equals(User.ADMIN.getEmail())) {
            throw new BadRequestException(Translator.toLocale(Message.USER_LOCK_FAILED.getKey(), null));
        }

        user.setActive(false);
        user.setRole(null);
        userRepository.save(user);

        systemLogService.createLog(user.getId(), Message.LOCK.getKey(), Message.USER_LOCK_SUCCESS.getKey());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('UNLOCK_USER', 'ROLE_ADMIN')")
    public void unLockUser(String id) throws Exception {
        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null)));

        if (user.getActive()) {
            throw new BadRequestException(Translator.toLocale(Message.ACCOUNT_ACTIVE.getKey(), null));
        }

        user.setActive(true);
        RoleEntity role = roleRepository.findByName(Role.USER.getName()).orElse(null);
        user.setRole(role);
        userRepository.save(user);

        systemLogService.createLog(user.getId(), Message.UNLOCK.getKey(), Message.USER_UNLOCK_SUCCESS.getKey());
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        log.info("Retrieving user by email {}", email);

        return userRepository.findByEmail(email).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null))
        );
    }

    @Override
    public void updateRefreshToken(String id, String token) {
        log.info("Updating token {}", token);

        UserEntity user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null)));

        user.setRefreshToken(token);

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByRefreshToken(String refreshToken) {
        log.info("Retrieving user by refresh token {}", refreshToken);

        return userRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null))
        );
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void changePassword(UserRequest request) throws Exception {
        log.info("Changing password {}", request.toString());

        checkCurrentUser(request.getId());

        UserEntity user = userRepository.findById(request.getId()).orElseThrow(() ->
                new NotFoundException(Translator.toLocale(Message.USER_NOT_FOUND.getKey(), null)));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException(Translator.toLocale(Message.PASSWORD_INCORRECT.getKey(), null));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(Translator.toLocale(Message.PASSWORD_NOT_MATCHED.getKey(), null));
        }

        String newPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newPassword);

        userRepository.save(user);

        systemLogService.createLog(
                user.getId(),
                Message.PASSWORD_CHANGE.getKey(),
                Message.PASSWORD_CHANGE_SUCCESS.getKey()
        );
    }

    public void checkCurrentUser(String id) throws Exception {
        String currentUser = SecurityUtil.getCurrentUserLogin().orElse(null);

        if (currentUser != null && !currentUser.equals(User.ADMIN.getEmail())) {
            String userId = SecurityUtil.getCurrentUserId().orElse(null);

            if (userId == null || !userId.equals(id)) {
                throw new AccessDeniedException(Translator.toLocale(Message.ACCESS_DENIED.getKey(), null));
            }
        }
    }
}
