package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Author;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    Set<Author> findByFullName(String fullName);
}
