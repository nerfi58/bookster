package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Genre;
import org.springframework.data.repository.CrudRepository;

public interface GenreRepository extends CrudRepository<Genre, Long> {
}
