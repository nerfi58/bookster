package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.Author;
import io.github.nerfi58.bookster.repositories.AuthorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(4)
public class InitializeAuthors implements CommandLineRunner {

    private final AuthorRepository authorRepository;

    public InitializeAuthors(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void run(String... args) {
        Author author1 = new Author();
        author1.setFullName("Marek Kowalski");

        Author author2 = new Author();
        author2.setFullName("Jan Nowak");

        Author author3 = new Author();
        author3.setFullName("Aleksander Zawieja");

        authorRepository.saveAll(List.of(author1, author2, author3));
    }
}
