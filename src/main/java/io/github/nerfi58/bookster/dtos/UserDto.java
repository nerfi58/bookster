package io.github.nerfi58.bookster.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;

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

    private LocalDate created;

    private List<String> roles;

    private String confirmationToken;
}
