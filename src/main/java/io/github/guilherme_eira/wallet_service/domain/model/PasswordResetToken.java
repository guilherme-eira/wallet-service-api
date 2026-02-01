package io.github.guilherme_eira.wallet_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class PasswordResetToken {
    private Long id;
    private User user;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime tokenExpiration;

    public static PasswordResetToken create(User user){
        return new PasswordResetToken(
                null,
                user,
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15)
        );
    }

    public PasswordResetToken(Long id, User user, String token, LocalDateTime createdAt, LocalDateTime tokenExpiration) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.createdAt = createdAt;
        this.tokenExpiration = tokenExpiration;
    }

    public PasswordResetToken() {
    }

    public Boolean isExpired(){
        return LocalDateTime.now().isAfter(tokenExpiration);
    }

    public Boolean isResendBlocked(){
        return LocalDateTime.now().isBefore(createdAt.plusMinutes(3));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(LocalDateTime tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }
}
