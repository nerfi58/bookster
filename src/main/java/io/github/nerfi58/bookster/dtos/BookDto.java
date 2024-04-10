package io.github.nerfi58.bookster.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class BookDto {

    private Long id;

    private String title;

    private Set<String> genres;

    private Set<AuthorDto> authors;

    private PublisherDto publisher;

    private Set<UserDto> usersWhoRead;
}
