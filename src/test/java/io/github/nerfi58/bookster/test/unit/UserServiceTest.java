package io.github.nerfi58.bookster.test.unit;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.events.OnRegistrationCompleteEvent;
import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import io.github.nerfi58.bookster.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.ClockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClockProvider fixedClockProvider;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

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
                passwordEncoder,
                fixedClockProvider,
                applicationEventPublisher
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
        given(userRepository.findUserByUsername("testuser")).willReturn(Optional.of(savedUserMock[0]));

        UserDto savedUserDto = userService.registerUser(userDtoToBeSaved);

        assertThat(savedUserDto).isNotNull();
        assertThat(savedUserDto.getId()).isEqualTo(1);
        assertThat(savedUserDto.getUsername()).isEqualTo(userDtoToBeSaved.getUsername());
        assertThat(savedUserDto.getEmail()).isEqualTo(userDtoToBeSaved.getEmail());
        assertThat(savedUserDto.getCreated().toString()).isEqualTo("2024-01-15");
        assertThat(savedUserDto.getRoles()).hasSize(1);
        assertThat(savedUserDto.getRoles().stream().findFirst().orElseThrow(EntityExistsException::new)).isEqualTo(
                "USER");
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
        given(userRepository.findUserByUsername("testuserwithsomeuppercaseletters")).willReturn(Optional.of(
                savedUserMock[0]));

        UserDto savedUserDto = userService.registerUser(userDtoToBeSaved);

        assertThat(savedUserDto.getUsername()).isEqualTo(userDtoToBeSaved.getUsername().toLowerCase());
        assertThat(savedUserDto.getEmail()).isEqualTo(userDtoToBeSaved.getEmail().toLowerCase());
    }

    @Test
    void givenUserDto_whenRegisterUser_thenPublishOnRegistrationCompleteEvent() {

        UserDto userDtoToBeSaved = UserDto.builder()
                .username("RandomTestUsername")
                .rawPassword("password")
                .email("TestEmail@WithUppercase.Letters")
                .build();

        given(userRepository.save(any(User.class))).will(i -> {
            User user = i.getArgument(0);
            user.setId(1);
            return user;
        });

        userService.registerUser(userDtoToBeSaved);

        ArgumentCaptor<OnRegistrationCompleteEvent> eventArgumentCaptor = ArgumentCaptor.forClass(
                OnRegistrationCompleteEvent.class);

        then(applicationEventPublisher).should().publishEvent(eventArgumentCaptor.capture());

        assertThat(eventArgumentCaptor.getValue()).isNotNull();
        assertThat(eventArgumentCaptor.getValue().getUserId()).isEqualTo(1L);
    }
}