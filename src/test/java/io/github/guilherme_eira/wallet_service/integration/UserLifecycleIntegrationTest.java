package io.github.guilherme_eira.wallet_service.integration;

import io.github.guilherme_eira.wallet_service.application.dto.input.LoginCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCredentialsException;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.service.LoginService;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class UserLifecycleIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Ciclo Completo: Criação -> Soft Delete -> Bloqueio de Login -> Reuso de E-mail e CPF")
    void shouldHandleSoftDeleteAndDataReuseCorrectly() {
        String email = "lifecycle@test.com";
        String taxId = "98028522017";
        String rawPassword = "123";

        User originalUser = new User();
        originalUser.setId(UUID.randomUUID());
        originalUser.setEmail(email);
        originalUser.setTaxId(taxId);
        originalUser.setPassword(passwordEncoder.encode(rawPassword));
        originalUser.setActive(true);
        originalUser.setVerified(true);
        originalUser.setType(UserType.COMMON);
        originalUser.setName("Original User");
        originalUser.setCreatedAt(LocalDateTime.now());
        originalUser.setLoginAttempts(0);
        originalUser.setTwoFactorActive(false);

        userRepository.save(originalUser);

        Assertions.assertDoesNotThrow(() ->
                        loginService.execute(new LoginCommand(email, rawPassword)),
                "Usuário ativo deveria conseguir logar"
        );

        originalUser.setActive(false);
        originalUser.setDeletedAt(LocalDateTime.now());
        userRepository.save(originalUser);

        Assertions.assertThrows(IncorrectCredentialsException.class, () ->
                        loginService.execute(new LoginCommand(email, rawPassword)),
                "Usuário inativo NÃO deveria conseguir logar"
        );

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode("newPass"));
        newUser.setTaxId(taxId);
        newUser.setActive(true);
        newUser.setVerified(true);
        newUser.setType(UserType.COMMON);
        newUser.setName("New User Recycled Email");
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setLoginAttempts(0);
        newUser.setTwoFactorActive(false);

        User savedNewUser = userRepository.save(newUser);

        Assertions.assertNotNull(savedNewUser.getId());

        Assertions.assertDoesNotThrow(() ->
                        loginService.execute(new LoginCommand(email, "newPass")),
                "Novo usuário com e-mail reciclado deveria conseguir logar"
        );

        long count = entityManager.createQuery(
                        "SELECT count(u) FROM UserEntity u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();

        Assertions.assertEquals(2, count, "Deveriam existir 2 registros físicos...");
    }
}