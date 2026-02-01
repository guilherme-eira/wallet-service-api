package io.github.guilherme_eira.wallet_service.domain.model;

import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String taxId;
    private String email;
    private String password;
    private UserType type;
    private Boolean active;
    private Boolean verified;
    private Integer loginAttempts;
    private LocalDateTime loginBlockedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean twoFactorActive;
    private String twoFactorSecret;

    public static User create(String name, String taxId, String email, String password, UserType type){
        return new User(
                UUID.randomUUID(),
                name,
                taxId,
                email,
                password,
                type,
                true,
                false,
                0,
                null,
                LocalDateTime.now(),
                null,
                null,
                false,
                null
        );
    }

    public User(UUID id, String name, String taxId, String email, String password, UserType type, Boolean active, Boolean verified, Integer loginAttempts, LocalDateTime loginBlockedUntil, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, boolean twoFactorActive, String twoFactorSecret) {
        this.id = id;
        this.name = name;
        this.taxId = taxId;
        this.email = email;
        this.password = password;
        this.type = type;
        this.active = active;
        this.verified = verified;
        this.loginAttempts = loginAttempts;
        this.loginBlockedUntil = loginBlockedUntil;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.twoFactorActive = twoFactorActive;
        this.twoFactorSecret = twoFactorSecret;
    }

    public User() {
    }

    public Boolean isLoginBlocked(){
        return loginBlockedUntil != null && LocalDateTime.now().isBefore(loginBlockedUntil);
    }

    public void resetAttempts(){
        this.loginAttempts = 0;
    }

    public void incrementAttempts(){
        this.loginAttempts = this.loginAttempts + 1;
    }

    public void enableMfa(String secret){
        this.twoFactorActive = true;
        this.twoFactorSecret = secret;
    }

    public void disableMfa(){
        this.twoFactorActive = false;
        this.twoFactorSecret = null;
    }

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getTaxId() {return taxId;}
    public void setTaxId(String taxId) {this.taxId = taxId;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public UserType getType() {return type;}
    public void setType(UserType type) {this.type = type;}

    public Boolean isActive() {return active;}
    public void setActive(Boolean active) {this.active = active;}

    public Boolean isVerified() {return verified;}
    public void setVerified(Boolean verified) {this.verified = verified;}

    public Integer getLoginAttempts() {return loginAttempts;}
    public void setLoginAttempts(Integer loginAttempts) {this.loginAttempts = loginAttempts;}

    public LocalDateTime getLoginBlockedUntil() {return loginBlockedUntil;}
    public void setLoginBlockedUntil(LocalDateTime loginBlockedUntil) {this.loginBlockedUntil = loginBlockedUntil;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public LocalDateTime getDeletedAt() {return deletedAt;}
    public void setDeletedAt(LocalDateTime deletedAt) {this.deletedAt = deletedAt;}

    public boolean isTwoFactorActive() {return twoFactorActive;}
    public void setTwoFactorActive(boolean twoFactorActive) {this.twoFactorActive = twoFactorActive;}

    public String getTwoFactorSecret() {return twoFactorSecret;}
    public void setTwoFactorSecret(String twoFactorSecret) {this.twoFactorSecret = twoFactorSecret;}
}
