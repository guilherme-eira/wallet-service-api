package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.UserEntity;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = "wiremock.server.port=8080")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldDeleteUnverifiedUsersOnlyIfOlderThanCutoff() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusHours(24);

        var userOld = createUser(false, now.minusHours(25));

        var userNew = createUser(false, now.minusHours(23));

        var userVerified = createUser(true, now.minusHours(25));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByVerifiedFalseAndCreatedAtBefore(cutoff);

        entityManager.flush();

        Assertions.assertFalse(repository.findById(userOld.getId()).isPresent(), "Deveria ter apagado o usuário antigo");
        Assertions.assertTrue(repository.findById(userNew.getId()).isPresent(), "Não deveria apagar usuário novo");
        Assertions.assertTrue(repository.findById(userVerified.getId()).isPresent(), "Não deveria apagar usuário verificado");
    }

    private UserEntity createUser(boolean verified, LocalDateTime createdAt) {
        UserEntity u = new UserEntity();
        u.setId(UUID.randomUUID());
        u.setName("Test Repo");
        u.setEmail(UUID.randomUUID() + "@test.com");
        u.setTaxId(UUID.randomUUID().toString().substring(0, 11));
        u.setPassword("pass");
        u.setType(UserType.COMMON);
        u.setActive(true);
        u.setVerified(verified);
        u.setLoginAttempts(0);
        u.setCreatedAt(createdAt);
        return entityManager.persist(u);
    }
}