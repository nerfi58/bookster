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

        Author robertCMartin = new Author();
        robertCMartin.setFullName("Robert C. Martin");

        Author laurentiuSpilca = new Author();
        laurentiuSpilca.setFullName("Laurentiu Spilca");

        Author jkRowling = new Author();
        jkRowling.setFullName("J.K. Rowling");

        authorRepository.saveAll(List.of(robertCMartin, laurentiuSpilca, jkRowling));
    }
}
