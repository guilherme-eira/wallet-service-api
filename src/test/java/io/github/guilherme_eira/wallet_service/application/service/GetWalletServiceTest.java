package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.output.WalletOutput;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.WalletOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetWalletServiceTest {

    @Mock WalletRepository walletRepository;
    @Mock WalletOutputMapper outputMapper;

    @InjectMocks GetWalletService service;

    @Test
    void shouldReturnWalletWhenExists() {
        var userId = UUID.randomUUID();
        var wallet = new Wallet();
        var output = new WalletOutput(null, null, null, null, null);

        given(walletRepository.findByUserId(userId)).willReturn(Optional.of(wallet));
        given(outputMapper.toWalletOutput(wallet)).willReturn(output);

        var result = service.execute(userId);

        Assertions.assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        given(walletRepository.findByUserId(any())).willReturn(Optional.empty());

        Assertions.assertThrows(WalletNotFoundException.class, () -> {
            service.execute(UUID.randomUUID());
        });
    }
}