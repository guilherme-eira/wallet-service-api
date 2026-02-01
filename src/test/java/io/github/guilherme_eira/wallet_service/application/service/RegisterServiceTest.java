package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.RegisterCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.RegisterOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserAlreadyExistsException;
import io.github.guilherme_eira.wallet_service.application.mapper.AuthOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.*;
import io.github.guilherme_eira.wallet_service.domain.enumeration.UserType;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import io.github.guilherme_eira.wallet_service.domain.model.VerificationToken;
import io.github.guilherme_eira.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock UserRepository userRepository;
    @Mock WalletRepository walletRepository;
    @Mock VerificationTokenRepository verificationTokenRepository;
    @Mock NotificationGateway notificationGateway;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthOutputMapper authMapper;

    @InjectMocks RegisterService registerService;

    @Test
    void shouldRegisterUserSuccessfully() {

        var cmd = new RegisterCommand(
                "John Doe",
                "98028522017",
                "john@test.com",
                "Password123!",
                UserType.COMMON,
                "1264"
        );

        User savedUser = new User();
        savedUser.setEmail("john@test.com");

        given(userRepository.existsByEmailOrTaxId(any(), any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encoded_hash");
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(authMapper.toRegisterOutput(any(), eq(true))).willReturn(new RegisterOutput(
                null, null, null, null, null
        ));

        registerService.execute(cmd);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        Assertions.assertEquals("encoded_hash", userCaptor.getValue().getPassword());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(walletCaptor.capture());
        Assertions.assertEquals("encoded_hash", walletCaptor.getValue().getTransactionPin());

        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(notificationGateway).sendVerificationEmail(eq("john@test.com"), any());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {

        var cmd = new RegisterCommand("John", "98028522017", "john@test.com", "Password01", UserType.COMMON, "1264");

        given(userRepository.existsByEmailOrTaxId(any(), any())).willReturn(true);

        Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            registerService.execute(cmd);
        });

        verify(userRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
        verify(notificationGateway, never()).sendVerificationEmail(any(), any());
    }
}