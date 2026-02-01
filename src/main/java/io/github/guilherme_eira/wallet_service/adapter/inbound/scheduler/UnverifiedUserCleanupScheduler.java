package io.github.guilherme_eira.wallet_service.adapter.inbound.scheduler;

import io.github.guilherme_eira.wallet_service.application.port.in.DeleteUnverifiedUsersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnverifiedUserCleanupScheduler {

    private final DeleteUnverifiedUsersUseCase deleteUnverifiedUsersUseCase;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void removeUnverifiedUsers() {
        log.info("Iniciando limpeza de usuários não verificados...");

        try {
            deleteUnverifiedUsersUseCase.execute();
            log.info("Limpeza concluída com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao limpar usuários não verificados: {}", e.getMessage());
        }
    }
}