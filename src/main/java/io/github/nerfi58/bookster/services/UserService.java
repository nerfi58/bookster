package io.github.nerfi58.bookster.services;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.ConfirmationToken;
import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.TokenNotValidException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import io.github.nerfi58.bookster.mappers.UserMapper;
import io.github.nerfi58.bookster.repositories.ConfirmationTokenRepository;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ClockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    static final int TOKEN_EXPIRATION_IN_SECONDS = 60 * 60 * 24;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       ConfirmationTokenRepository confirmationTokenRepository, PasswordEncoder passwordEncoder,
                       ClockProvider clockProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clockProvider.getClock();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        return user.orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public UserDto registerUser(UserDto userDto) {
        userDto.setUsername(userDto.getUsername().toLowerCase());
        userDto.setEmail(userDto.getEmail().toLowerCase());
        userDto.setPasshash(passwordEncoder.encode(userDto.getRawPassword()));

        Role userRole = roleRepository.findByRole(RoleEnum.USER).orElseThrow(EntityNotFoundException::new);

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User user = UserMapper.userDtoToUser(userDto);
        user.setRoles(List.of(userRole));
        user.setCreated(LocalDate.now(clock));

        User savedUser = userRepository.save(user);
        generateConfirmationToken(savedUser.getId());

        return UserMapper.userToUserDto(savedUser);
    }

    public ConfirmationToken generateConfirmationToken(long userId) {

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken(UUID.randomUUID().toString());
        confirmationToken.setUser(user);
        confirmationToken.setCreatedAt(LocalDateTime.now(clock));
        confirmationToken.setExpiresAt(LocalDateTime.now(clock).plusSeconds(TOKEN_EXPIRATION_IN_SECONDS));

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
