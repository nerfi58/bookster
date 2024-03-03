package io.github.nerfi58.bookster.services;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.exceptions.EmailAlreadyExistsException;
import io.github.nerfi58.bookster.exceptions.UsernameAlreadyExistsException;
import io.github.nerfi58.bookster.mappers.UserMapper;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import io.github.nerfi58.bookster.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final Clock clock = Clock.systemUTC();

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(UserDto userDto) {
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
        user.setRoles(Set.of(userRole));
        user.setCreated(Date.from(Instant.now(clock)));

        return userRepository.save(user);
    }
}
