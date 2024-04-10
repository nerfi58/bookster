package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Publisher;
import org.springframework.data.repository.CrudRepository;

public interface PublisherRepository extends CrudRepository<Publisher, Long> {
}
