package io.github.nerfi58.bookster.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthorDto {

    private Long id;

    private String fullName;
}
