package io.github.nerfi58.bookster.services;

import io.github.nerfi58.bookster.entities.ConfirmationToken;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.events.OnTokenGenerationCompleteEvent;
import io.github.nerfi58.bookster.exceptions.TokenNotValidException;
import io.github.nerfi58.bookster.repositories.ConfirmationTokenRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ClockProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserConfirmationService {

    static final int TOKEN_EXPIRATION_IN_SECONDS = 60 * 60 * 24;

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final Clock clock;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserConfirmationService(UserRepository userRepository,
                                   ConfirmationTokenRepository confirmationTokenRepository,
                                   ClockProvider clockProvider,
                                   ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.clock = clockProvider.getClock();
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ConfirmationToken generateConfirmationToken(long userId) {

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        if (user.isActive()) {
            throw new TokenNotValidException();
        }

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setUser(user);
        confirmationToken.setCreatedAt(LocalDateTime.now(clock));
        confirmationToken.setExpiresAt(LocalDateTime.now(clock).plusSeconds(TOKEN_EXPIRATION_IN_SECONDS));

        applicationEventPublisher.publishEvent(new OnTokenGenerationCompleteEvent(
                confirmationToken.getUser(),
                confirmationToken.getToken()
        ));

        return confirmationTokenRepository.save(confirmationToken);
    }

    public void activateUserAccount(String token) {

        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token)
                .orElseThrow(TokenNotValidException::new);

        //if token was already used or is expired
        if (confirmationToken.getConfirmedAt() != null ||
            LocalDateTime.now(clock).isAfter(confirmationToken.getExpiresAt())) {

            throw new TokenNotValidException();
        }

        User user = confirmationToken.getUser();
        user.setActive(true);
        confirmationToken.setConfirmedAt(LocalDateTime.now(clock));

        confirmationTokenRepository.save(confirmationToken);
        userRepository.save(user);
    }
}
