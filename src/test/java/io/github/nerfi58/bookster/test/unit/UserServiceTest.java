package io.github.nerfi58.bookster.test.unit;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import io.github.nerfi58.bookster.services.UserService;
import jakarta.validation.ClockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClockProvider fixedClockProvider;

    private UserService userService;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setRole(RoleEnum.USER);
        given(roleRepository.findByRole(RoleEnum.USER)).willReturn(Optional.of(userRole));

        given(fixedClockProvider.getClock()).willReturn(Clock.fixed(
                Instant.parse("2024-01-15T16:45:42.00Z"),
                ZoneId.of("Europe/Warsaw")
        ));

        this.userService = new UserService(userRepository, roleRepository, passwordEncoder, fixedClockProvider);
    }

    @Test
    void givenUserDto_whenSaveUser_thenConvertDtoToEntityAndSaveThatEntity() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("email@example.com")
                .build();

        given(passwordEncoder.encode("password")).willReturn(
                "$2a$12$GbZ1YG.3GBIqPUfkQmzV3eOzJCPM2vhF9DSXuFzIi7MBQsI3WUoRC");
        given(userRepository.save(any(User.class))).will((i) -> {
            User savedUser = i.getArgument(0);
            savedUser.setId(1);
            return savedUser;
        });

        UserDto savedUser = userService.saveUser(userDtoToBeSaved);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isEqualTo(1);
        assertThat(savedUser.getUsername()).isEqualTo(userDtoToBeSaved.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(userDtoToBeSaved.getEmail());
        assertThat(savedUser.getCreated()).isEqualTo(LocalDate.now(fixedClockProvider.getClock()));
        assertThat(savedUser.getRoles()).hasSize(1);
        assertThat(savedUser.getRoles().getFirst()).isEqualTo("USER");
    }

    @Test
    void givenUserDto_whenUserWithThisUsernameAlreadyExists_thenThrowException() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("email@example.com")
                .build();

        given(userRepository.existsByUsername(userDtoToBeSaved.getUsername().toLowerCase())).willReturn(true);

        assertThatExceptionOfType(UsernameAlreadyExistsException.class).isThrownBy(() -> {
            userService.saveUser(userDtoToBeSaved);
        });
    }

    @Test
    void givenUserDto_whenUserWithThisEmailAlreadyExists_thenThrowException() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("email@example.com")
                .build();

        given(userRepository.existsByEmail(userDtoToBeSaved.getEmail().toLowerCase())).willReturn(true);

        assertThatExceptionOfType(EmailAlreadyExistsException.class).isThrownBy(() -> {
            userService.saveUser(userDtoToBeSaved);
        });
    }
}