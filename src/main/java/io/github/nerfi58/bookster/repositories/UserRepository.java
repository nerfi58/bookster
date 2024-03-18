package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
