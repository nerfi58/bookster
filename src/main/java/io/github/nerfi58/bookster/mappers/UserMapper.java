package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.dtos.UserDto;
import io.github.nerfi58.bookster.entities.User;

public class UserMapper {

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
