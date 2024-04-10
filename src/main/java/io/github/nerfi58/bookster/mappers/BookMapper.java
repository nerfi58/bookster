package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.dtos.BookDto;
import io.github.nerfi58.bookster.entities.Book;

import java.util.stream.Collectors;

public class BookMapper {

    public static BookDto bookToBookDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .genres(book.getGenres().stream().map(e -> e.getGenre().name()).collect(Collectors.toSet()))
                .authors(book.getAuthors().stream().map(AuthorMapper::authorToAuthorDto).collect(Collectors.toSet()))
                .publisher(PublisherMapper.publisherToPublisherDto(book.getPublisher()))
                .usersWhoRead(book.getUsersWhoRead()
                                      .stream()
                                      .map(UserMapper::userToUserDto)
                                      .collect(Collectors.toSet()))
                .build();
    }
}
