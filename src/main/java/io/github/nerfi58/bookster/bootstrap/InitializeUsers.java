package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ClockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Component
@Order(2)
public class InitializeUsers implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Autowired
    public InitializeUsers(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, ClockProvider clockProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clockProvider.getClock();
    }

    @Override
    public void run(String... args) {

        Role userRole = roleRepository.findByRole(RoleEnum.USER).orElseThrow(EntityNotFoundException::new);
        Role moderaratorRole = roleRepository.findByRole(RoleEnum.MODERATOR).orElseThrow(EntityNotFoundException::new);
        Role adminRole = roleRepository.findByRole(RoleEnum.ADMIN).orElseThrow(EntityNotFoundException::new);

        User user = new User();
        user.setUsername("user");
        user.setPasshash(passwordEncoder.encode("user"));
        user.setEmail("user@bookster.com");
        user.setCreated(LocalDate.now(clock));
        user.setActive(true);
        user.setRoles(List.of(userRole));
        userRepository.save(user);

        User moderator = new User();
        moderator.setUsername("moderator");
        moderator.setPasshash(passwordEncoder.encode("moderator"));
        moderator.setEmail("moderator@bookster.com");
        moderator.setCreated(LocalDate.now(clock));
        moderator.setActive(true);
        moderator.setRoles(List.of(userRole, moderaratorRole));
        userRepository.save(moderator);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPasshash(passwordEncoder.encode("admin"));
        admin.setEmail("admin@bookster.com");
        admin.setCreated(LocalDate.now(clock));
        admin.setActive(true);
        admin.setRoles(List.of(userRole, moderaratorRole, adminRole));
        userRepository.save(admin);
    }
}
