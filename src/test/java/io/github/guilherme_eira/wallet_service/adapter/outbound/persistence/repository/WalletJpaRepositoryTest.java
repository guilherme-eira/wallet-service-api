package io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.repository;

import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.UserEntity;
import io.github.guilherme_eira.wallet_service.adapter.outbound.persistence.entity.WalletEntity;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@DataJpaTest
@ActiveProfiles("test")
class WalletJpaRepositoryTest {

    @Autowired
    private WalletJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionTemplate txTemplate;

    @Test
    void shouldBlockSecondTransactionWhenQueryingByOwner() throws InterruptedException {

        var user = createUser("98028522017");
        createWallet(user);

        TestTransaction.flagForCommit();
        TestTransaction.end();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch lockAcquiredSignal = new CountDownLatch(1);
        AtomicLong t1FinishTime = new AtomicLong();
        AtomicLong t2FinishTime = new AtomicLong();

        executor.submit(() -> {
            txTemplate.execute(status -> {
                System.out.println("T1: Buscando wallet com Lock...");
                repository.findByOwnerWithLock(user);
                System.out.println("T1: Lock adquirido! Segurando por 500ms...");
                lockAcquiredSignal.countDown();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                t1FinishTime.set(System.currentTimeMillis());
                System.out.println("T1: Finalizando transação (Commit)");
                return null;
            });
        });

        executor.submit(() -> {
            try {
                lockAcquiredSignal.await();
                Thread.sleep(50);

                System.out.println("T2: Tentando buscar com Lock...");
                txTemplate.execute(status -> {
                    repository.findByOwnerWithLock(user);
                    t2FinishTime.set(System.currentTimeMillis());
                    System.out.println("T2: Conseguiu o Lock (após T1 liberar)!");
                    return null;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread.sleep(1200);
        executor.shutdown();

        Assertions.assertTrue(t2FinishTime.get() > t1FinishTime.get(),
                "A Thread 2 deveria ter esperado a Thread 1 liberar o lock, mas não esperou.");
    }

    private UserEntity createUser(String taxId) {
        UserEntity u = new UserEntity();
        u.setId(UUID.randomUUID());
        u.setName("Test Lock");
        u.setTaxId(taxId);
        u.setEmail("lock" + UUID.randomUUID() + "@test.com");
        u.setPassword("pass");
        u.setType(UserType.COMMON);
        u.setActive(true);
        u.setVerified(true);
        u.setLoginAttempts(0);
        u.setCreatedAt(LocalDateTime.now());
        u.setTwoFactorActive(false);
        entityManager.persist(u);
        return u;
    }

    private void createWallet(UserEntity owner) {
        WalletEntity w = new WalletEntity();
        w.setId(UUID.randomUUID());
        w.setOwner(owner);
        w.setBalance(BigDecimal.ZERO);
        w.setTransactionPin("6492");
        w.setPinAttempts(0);
        w.setTransactionLimit(new BigDecimal(2000));
        w.setNightLimit(new BigDecimal(1000));
        w.setDailyLimit(new BigDecimal(5000));
        w.setCreatedAt(LocalDateTime.now());
        entityManager.persist(w);
        entityManager.flush();
    }
}