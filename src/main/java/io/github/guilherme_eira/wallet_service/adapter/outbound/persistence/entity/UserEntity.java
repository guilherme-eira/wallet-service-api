package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String taxId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType type;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean verified;

    @Column(nullable = false)
    private Integer loginAttempts;

    private LocalDateTime loginBlockedUntil;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private boolean twoFactorActive;

    private String twoFactorSecret;
}