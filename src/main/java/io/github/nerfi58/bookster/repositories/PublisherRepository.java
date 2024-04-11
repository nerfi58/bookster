package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Publisher;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PublisherRepository extends CrudRepository<Publisher, Long> {

    Optional<Publisher> findByName(String name);
}
