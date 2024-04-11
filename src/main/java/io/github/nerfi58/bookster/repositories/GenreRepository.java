package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Genre;
import io.github.nerfi58.bookster.entities.enums.GenreEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GenreRepository extends CrudRepository<Genre, Long> {

    Optional<Genre> findByGenre(GenreEnum genre);
}
