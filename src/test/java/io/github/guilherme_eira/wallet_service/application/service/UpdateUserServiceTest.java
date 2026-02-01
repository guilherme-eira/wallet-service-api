package io.github.guilherme_eira.wallet_service.application.service;

import io.github.guilherme_eira.wallet_service.application.dto.input.UpdateUserCommand;
import io.github.guilherme_eira.wallet_service.application.dto.output.UpdateUserOutput;
import io.github.guilherme_eira.wallet_service.application.exception.UserNotFoundException;
import io.github.guilherme_eira.wallet_service.application.mapper.UserOutputMapper;
import io.github.guilherme_eira.wallet_service.application.port.out.UserRepository;
import io.github.guilherme_eira.wallet_service.domain.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    UserOutputMapper mapper;

    @InjectMocks
    UpdateUserService service;

    @Test
    void shouldUpdateUserWhenNameChanged() {
        var id = UUID.randomUUID();
        var cmd = new UpdateUserCommand(id, "New Name");

        var user = new User();
        user.setId(id);
        user.setName("Old Name");

        given(repository.findById(id)).willReturn(Optional.of(user));
        given(repository.save(any(User.class))).willReturn(user);
        given(mapper.toUpdateUserOutput(any())).willReturn(new UpdateUserOutput(id, "New Name", null, null));

        service.execute(cmd);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());

        var savedUser = captor.getValue();
        Assertions.assertEquals("New Name", savedUser.getName());
        Assertions.assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void shouldNotUpdateWhenNameIsSame() {
        var id = UUID.randomUUID();
        var cmd = new UpdateUserCommand(id, "Same Name");

        var user = new User();
        user.setId(id);
        user.setName("Same Name");
        user.setUpdatedAt(null);

        given(repository.findById(id)).willReturn(Optional.of(user));
        given(mapper.toUpdateUserOutput(user)).willReturn(new UpdateUserOutput(id, "Same Name", null, null));

        service.execute(cmd);

        verify(repository, never()).save(any());
        Assertions.assertNull(user.getUpdatedAt());
    }

    @Test
    void shouldNotUpdateWhenNameIsNull() {
        var id = UUID.randomUUID();
        var cmd = new UpdateUserCommand(id, null);

        var user = new User();
        user.setId(id);
        user.setName("Old Name");

        given(repository.findById(id)).willReturn(Optional.of(user));
        given(mapper.toUpdateUserOutput(user)).willReturn(new UpdateUserOutput(id, "Old Name", null, null));

        service.execute(cmd);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        var cmd = new UpdateUserCommand(UUID.randomUUID(), "Name");

        given(repository.findById(any())).willReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> service.execute(cmd));

        verify(repository, never()).save(any());
    }
}