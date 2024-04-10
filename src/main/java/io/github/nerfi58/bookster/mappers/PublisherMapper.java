package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.dtos.PublisherDto;
import io.github.nerfi58.bookster.entities.Publisher;

public class PublisherMapper {

    public static PublisherDto publisherToPublisherDto(Publisher publisher) {
        return PublisherDto.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .build();
    }
}
