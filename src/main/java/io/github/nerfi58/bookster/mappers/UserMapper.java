package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.security.UserDetailsJpa;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserDetailsJpa userToUserDetails(User user) {
        UserDetailsJpa userDetails = new UserDetailsJpa();
        userDetails.setUsername(user.getUsername());
        userDetails.setPasshash(user.getPasshash());
        userDetails.setActive(user.isActive());
        userDetails.setRoles(user.getRoles()
                                     .stream()
                                     .map(e -> (GrantedAuthority) () -> "ROLE_" + e.getRole().name())
                                     .collect(Collectors.toList())
        );

        return userDetails;
    }

    public static User userDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasshash(userDto.getPasshash());

        return user;
    }

    public static UserDto userToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .created(user.getCreated())
                .roles(user.getRoles().stream().map(e -> e.getRole().name()).toList())
                .build();
    }
}
