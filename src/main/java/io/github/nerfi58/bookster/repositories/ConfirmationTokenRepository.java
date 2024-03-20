package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.ConfirmationToken;
import io.github.nerfi58.bookster.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByUser(User user);
}
