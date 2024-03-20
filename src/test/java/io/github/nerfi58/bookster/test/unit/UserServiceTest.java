package io.github.nerfi58.bookster.test.unit;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.ConfirmationToken;
import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.TokenNotValidException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import io.github.nerfi58.bookster.repositories.ConfirmationTokenRepository;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import io.github.nerfi58.bookster.services.UserService;
import jakarta.validation.ClockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

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
                ZoneId.of("Europe/London")
        ));

        this.userService = new UserService(
                userRepository,
                roleRepository,
                confirmationTokenRepository,
                passwordEncoder,
                fixedClockProvider
        );
    }

    @Test
    void givenUserDto_whenRegisterUser_thenConvertDtoToEntityAndSaveThatEntity() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("email@example.com")
                .build();

        final User[] savedUserMock = new User[1];
        savedUserMock[0] = new User();

        given(passwordEncoder.encode("password")).willReturn(
                "$2a$12$GbZ1YG.3GBIqPUfkQmzV3eOzJCPM2vhF9DSXuFzIi7MBQsI3WUoRC");
        given(userRepository.save(any(User.class))).will((i) -> {
            savedUserMock[0] = i.getArgument(0);
            savedUserMock[0].setId(1);
            return savedUserMock[0];
        });
        given(userRepository.findById(1L)).willReturn(Optional.of(savedUserMock[0]));

        UserDto savedUserDto = userService.registerUser(userDtoToBeSaved);

        assertThat(savedUserDto).isNotNull();
        assertThat(savedUserDto.getId()).isEqualTo(1);
        assertThat(savedUserDto.getUsername()).isEqualTo(userDtoToBeSaved.getUsername());
        assertThat(savedUserDto.getEmail()).isEqualTo(userDtoToBeSaved.getEmail());
        assertThat(savedUserDto.getCreated().toString()).isEqualTo("2024-01-15");
        assertThat(savedUserDto.getRoles()).hasSize(1);
        assertThat(savedUserDto.getRoles().getFirst()).isEqualTo("USER");
    }

    @Test
    void givenUserDto_whenRegisterUserAndUserWithThisUsernameAlreadyExists_thenThrowException() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("email@example.com")
                .build();

        given(userRepository.existsByUsername(userDtoToBeSaved.getUsername().toLowerCase())).willReturn(true);

        assertThatExceptionOfType(UsernameAlreadyExistsException.class).isThrownBy(() -> userService.registerUser(
                userDtoToBeSaved));
    }

    @Test
    void givenUserDto_whenRegisterUserAndUserWithThisEmailAlreadyExists_thenThrowException() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("testUser")
                .rawPassword("password")
                .email("email@example.com")
                .build();

        given(userRepository.existsByEmail(userDtoToBeSaved.getEmail().toLowerCase())).willReturn(true);

        assertThatExceptionOfType(EmailAlreadyExistsException.class).isThrownBy(() -> userService.registerUser(
                userDtoToBeSaved));
    }

    @Test
    void givenUserDtoWithUppercaseUsernameAndEmail_whenRegisterUser_thenChangeUsernameAndEmailToLowercase() {
        UserDto userDtoToBeSaved = UserDto.builder()
                .username("TestUserWithSomeUppercaseLetters")
                .rawPassword("password")
                .email("TestEmail@WithUppercase.Letters")
                .build();

        final User[] savedUserMock = new User[1];
        savedUserMock[0] = new User();

        given(passwordEncoder.encode("password")).willReturn(
                "$2a$12$GbZ1YG.3GBIqPUfkQmzV3eOzJCPM2vhF9DSXuFzIi7MBQsI3WUoRC");
        given(userRepository.save(any(User.class))).will((i) -> {
            savedUserMock[0] = i.getArgument(0);
            savedUserMock[0].setId(1);
            return savedUserMock[0];
        });
        given(userRepository.findById(1L)).willReturn(Optional.of(savedUserMock[0]));

        UserDto savedUserDto = userService.registerUser(userDtoToBeSaved);

        assertThat(savedUserDto.getUsername()).isEqualTo(userDtoToBeSaved.getUsername().toLowerCase());
        assertThat(savedUserDto.getEmail()).isEqualTo(userDtoToBeSaved.getEmail().toLowerCase());
    }

    @Test
    void givenUserId_whenGenerateConfirmationToken_thenFindUserWithThatIdAndGenerateTokenForThatUser() {

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

            confirmationToken = userService.generateConfirmationToken(5L);
        }

        assertThat(confirmationToken).isNotNull();
        assertThat(confirmationToken.getId()).isEqualTo(1L);
        assertThat(confirmationToken.getUser()).isEqualTo(existingUser);
        assertThat(confirmationToken.getToken()).isEqualTo(uuidString);
        assertThat(confirmationToken.getCreatedAt().toString()).isEqualTo("2024-01-15T16:45:42");
        assertThat(confirmationToken.getExpiresAt().toString()).isEqualTo("2024-01-16T16:45:42");
        assertThat(confirmationToken.getConfirmedAt()).isNull();
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

        userService.activateUserAccount(uuidString);

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

        assertThatExceptionOfType(TokenNotValidException.class).isThrownBy(() -> userService.activateUserAccount(
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

        assertThatExceptionOfType(TokenNotValidException.class).isThrownBy(() -> userService.activateUserAccount(
                uuidString));
    }
}