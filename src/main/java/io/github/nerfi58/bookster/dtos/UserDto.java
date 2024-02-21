package io.github.nerfi58.bookster.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    @NotNull
    @Size(min = 5, max = 32)
    private String username;

    @Size(min = 8)
    private String rawPassword;

    @Size(min = 72, max = 72)
    private String passhash;

    @NotNull
    @Size(max = 72)
    @Email
    private String email;
}
