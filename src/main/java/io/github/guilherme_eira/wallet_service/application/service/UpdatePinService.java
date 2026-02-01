package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdatePinCommand;
import io.github.guilherme_eira.wallet_service.application.exception.IncorrectCurrentPasswordException;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.exception.WalletNotFoundException;
import io.github.guilherme_eira.wallet_service.application.port.in.UpdatePinUseCase;
import io.github.guilherme_eira.wallet_service.application.port.out.PasswordEncoder;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.application.port.out.WalletRepository;
import io.github.guilherme_eira.wallet_service.domain.valueobject.TransactionPin;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdatePinService implements UpdatePinUseCase {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void execute(UpdatePinCommand cmd) {
        var user = userRepository.findById(cmd.id())
                .orElseThrow(UserNotFoundException::new);

        if(!encoder.matches(cmd.currentPassword(), user.getPassword())){
            throw new IncorrectCurrentPasswordException();
        }

        var newPin = new TransactionPin(cmd.newPin());

        var wallet = walletRepository.findByUser(user)
                        .orElseThrow(WalletNotFoundException::new);

        wallet.setTransactionPin(encoder.encode(newPin.getValue()));
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);
    }
}
