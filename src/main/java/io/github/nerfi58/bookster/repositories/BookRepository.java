package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Book;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    
}
