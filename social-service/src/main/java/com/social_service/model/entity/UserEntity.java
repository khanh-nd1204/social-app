package com.social_service.model.entity;

import com.social_service.constant.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "email", nullable = false, unique = true, updatable = false, length = 100)
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Column(name = "address")
    String address;

    @Column(name = "phone", unique = true, length = 10)
    String phone;

    @Column(name = "birth_date")
    LocalDate birthDate;

    @Column(name = "verified", nullable = false)
    Boolean verified;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "bio", length = 1000)
    String bio;

    @Column(name = "avatar")
    String avatar;

    @Column(name = "refresh_token", length = 1000)
    String refreshToken;

    @Column(name = "otp")
    Integer otp;

    @Column(name = "otp_duration")
    Instant otpDuration;

    @Column(name = "google_id")
    String googleId;

    @Column(name = "active", nullable = false)
    Boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    RoleEntity role;
}
