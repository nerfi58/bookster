package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.dtos.AuthorDto;
import io.github.nerfi58.bookster.entities.Author;

public class AuthorMapper {

    public static AuthorDto authorToAuthorDto(Author author) {
        return AuthorDto.builder()
                .id(author.getId())
                .fullName(author.getFullName())
                .build();
    }
}
