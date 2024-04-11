package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.Genre;
import io.github.nerfi58.bookster.entities.enums.GenreEnum;
import io.github.nerfi58.bookster.repositories.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(3)
public class InitializeGenres implements CommandLineRunner {

    private final GenreRepository genreRepository;

    public InitializeGenres(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public void run(String... args) {

        Genre science = new Genre();
        science.setGenre(GenreEnum.SCIENCE);

        Genre thriller = new Genre();
        thriller.setGenre(GenreEnum.THRILLER);

        Genre fantasy = new Genre();
        fantasy.setGenre(GenreEnum.FANTASY);

        Genre mystery = new Genre();
        mystery.setGenre(GenreEnum.MYSTERY);

        genreRepository.saveAll(Set.of(science, thriller, fantasy, mystery));
    }
}
