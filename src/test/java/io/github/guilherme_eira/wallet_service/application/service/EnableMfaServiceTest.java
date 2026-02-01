package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.MfaSetupCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.out.MfaProvider;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnableMfaServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    MfaProvider mfaProvider;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    EnableMfaService enableMfaService;

    @Test
    void shouldEnableMfaAndReturnUrlWhenPasswordIsCorrect() {

        var userId = UUID.randomUUID();
        var email = "john@test.com";
        var password = "password123";
        var generatedSecret = "JBSWY3DPEHPK3PXP";

        var command = new MfaSetupCommand(userId, password);

        var user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setPassword("encoded_pass");
        user.setTwoFactorActive(false);

        BDDMockito.given(repository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(password, user.getPassword()))
                .willReturn(true);

        BDDMockito.given(mfaProvider.generateSecret())
                .willReturn(generatedSecret);

        String resultUrl = enableMfaService.execute(command);

        Assertions.assertTrue(user.isTwoFactorActive(), "MFA deve ser ativado");
        Assertions.assertEquals(generatedSecret, user.getTwoFactorSecret(), "O segredo deve ser salvo no usuário");
        Assertions.assertNotNull(user.getUpdatedAt());

        verify(repository).save(user);

        Assertions.assertTrue(resultUrl.startsWith("otpauth://totp/"));
        Assertions.assertTrue(resultUrl.contains(email));
        Assertions.assertTrue(resultUrl.contains(generatedSecret));
        Assertions.assertTrue(resultUrl.contains("issuer=Wallet Service API"));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        var userId = UUID.randomUUID();
        var command = new MfaSetupCommand(userId, "wrongPass");

        var user = new User();
        user.setPassword("realHash");

        BDDMockito.given(repository.findById(userId))
                .willReturn(Optional.of(user));

        BDDMockito.given(encoder.matches(any(), any()))
                .willReturn(false);

        Assertions.assertThrows(IncorrectCurrentPasswordException.class, () -> {
            enableMfaService.execute(command);
        });

        verify(mfaProvider, never()).generateSecret();
        verify(repository, never()).save(any());
        Assertions.assertFalse(user.isTwoFactorActive());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        var command = new MfaSetupCommand(UUID.randomUUID(), "pass");

        BDDMockito.given(repository.findById(any()))
                .willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            enableMfaService.execute(command);
        });

        verify(repository, never()).save(any());
    }
}