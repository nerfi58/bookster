package io.github.nerfi58.bookster.test.unit;

import io.github.nerfi58.bookster.entities.ConfirmationToken;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.events.OnTokenGenerationCompleteEvent;
import io.github.nerfi58.bookster.exceptions.TokenNotValidException;
import io.github.nerfi58.bookster.repositories.ConfirmationTokenRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import io.github.nerfi58.bookster.services.UserConfirmationService;
import jakarta.validation.ClockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@ActiveProfiles("test")
public class UserConfirmationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private ClockProvider fixedClockProvider;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private UserConfirmationService userConfirmationService;

    @BeforeEach
    void setUp() {

        given(fixedClockProvider.getClock()).willReturn(Clock.fixed(
                Instant.parse("2024-01-15T16:45:42.00Z"),
                ZoneId.of("Europe/London")
        ));

        userConfirmationService = new UserConfirmationService(
                userRepository,
                confirmationTokenRepository,
                fixedClockProvider,
                applicationEventPublisher
        );
    }

    @Test
    void givenUserId_whenGenerateConfirmationToken_thenGenerateTokenForThatUserAndPublishOnTokenGenerationCompleteEvent() {

        User existingUser = new User();
        existingUser.setId(5L);
        existingUser.setUsername("testUser");
        existingUser.setPasshash("$2a$12$GbZ1YG.3GBIqPUfkQmzV3eOzJCPM2vhF9DSXuFzIi7MBQsI3WUoRC");
        existingUser.setActive(false);

        String uuidString = "288a9919-ccbb-40f3-abb1-617bcfe43acb";
        UUID uuid = UUID.fromString(uuidString);

        given(userRepository.findById(5L)).willReturn(Optional.of(existingUser));
        given(confirmationTokenRepository.save(any(ConfirmationToken.class))).will(i -> {
            ConfirmationToken confirmationToken = i.getArgument(0);
            confirmationToken.setId(1L);
            return confirmationToken;
        });

        ConfirmationToken confirmationToken;

        try (MockedStatic<UUID> uuidMockedStatic = Mockito.mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(uuid);

            confirmationToken = userConfirmationService.generateConfirmationToken(5L);
        }

        ArgumentCaptor<OnTokenGenerationCompleteEvent> eventArgumentCaptor = ArgumentCaptor.forClass(
                OnTokenGenerationCompleteEvent.class);

        then(applicationEventPublisher).should().publishEvent(eventArgumentCaptor.capture());

        assertThat(confirmationToken).isNotNull();
        assertThat(confirmationToken.getId()).isEqualTo(1L);
        assertThat(confirmationToken.getUser()).isEqualTo(existingUser);
        assertThat(confirmationToken.getToken()).isEqualTo(uuidString);
        assertThat(confirmationToken.getCreatedAt().toString()).isEqualTo("2024-01-15T16:45:42");
        assertThat(confirmationToken.getExpiresAt().toString()).isEqualTo("2024-01-16T16:45:42");
        assertThat(confirmationToken.getConfirmedAt()).isNull();
        assertThat(eventArgumentCaptor.getValue()).isNotNull();
        assertThat(eventArgumentCaptor.getValue().getToken()).isEqualTo(uuidString);
    }

    @Test
    void givenToken_whenTokenIsValid_thenActivateUserAccountAssociatedWithThatToken() {

        String uuidString = "288a9919-ccbb-40f3-abb1-617bcfe43acb";

        User existingUser = new User();
        existingUser.setId(5L);
        existingUser.setUsername("testUser");
        existingUser.setPasshash("$2a$12$GbZ1YG.3GBIqPUfkQmzV3eOzJCPM2vhF9DSXuFzIi7MBQsI3WUoRC");
        existingUser.setActive(false);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setId(1);
        confirmationToken.setToken("288a9919-ccbb-40f3-abb1-617bcfe43acb");
        confirmationToken.setCreatedAt(LocalDateTime.parse("2024-01-15T13:45:42"));
        confirmationToken.setExpiresAt(LocalDateTime.parse("2024-01-16T13:45:42"));
        confirmationToken.setUser(existingUser);

        given(confirmationTokenRepository.findByToken(uuidString)).willReturn(Optional.of(confirmationToken));
        given(userRepository.save(any(User.class))).will(i -> i.getArgument(0));

        ArgumentCaptor<ConfirmationToken> tokenArgumentCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        userConfirmationService.activateUserAccount(uuidString);

        then(confirmationTokenRepository).should().save(tokenArgumentCaptor.capture());
        then(userRepository).should().save(userArgumentCaptor.capture());

        assertThat(tokenArgumentCaptor.getValue().getConfirmedAt().toString()).isEqualTo("2024-01-15T16:45:42");
        assertThat(userArgumentCaptor.getValue().isActive()).isTrue();
    }

    @Test
    void givenToken_whenTokenWasAlreadyActivated_thenThrowException() {

        String uuidString = "288a9919-ccbb-40f3-abb1-617bcfe43acb";

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setId(1);
        confirmationToken.setToken(uuidString);
        confirmationToken.setCreatedAt(LocalDateTime.parse("2024-01-15T13:45:42"));
        confirmationToken.setExpiresAt(LocalDateTime.parse("2024-01-16T13:45:42"));
        confirmationToken.setConfirmedAt(LocalDateTime.parse("2024-01-15T19:45:42"));

        given(confirmationTokenRepository.findByToken(uuidString)).willReturn(Optional.of(confirmationToken));

        assertThatExceptionOfType(TokenNotValidException.class).isThrownBy(() -> userConfirmationService.activateUserAccount(
                uuidString));
    }

    @Test
    void givenToken_whenTokenIsExpired_thenThrowException() {

        String uuidString = "288a9919-ccbb-40f3-abb1-617bcfe43acb";

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setId(1);
        confirmationToken.setToken(uuidString);
        confirmationToken.setCreatedAt(LocalDateTime.parse("2024-01-13T13:45:42"));
        confirmationToken.setExpiresAt(LocalDateTime.parse("2024-01-14T13:45:42"));

        given(confirmationTokenRepository.findByToken(uuidString)).willReturn(Optional.of(confirmationToken));

        assertThatExceptionOfType(TokenNotValidException.class).isThrownBy(() -> userConfirmationService.activateUserAccount(
                uuidString));
    }
}
