package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}
