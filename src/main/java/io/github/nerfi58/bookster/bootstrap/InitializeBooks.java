package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.*;
import io.github.nerfi58.bookster.entities.enums.GenreEnum;
import io.github.nerfi58.bookster.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Order(6)
public class InitializeBooks implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    public InitializeBooks(AuthorRepository authorRepository,
                           PublisherRepository publisherRepository,
                           BookRepository bookRepository,
                           GenreRepository genreRepository,
                           UserRepository userRepository) {
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Genre science = genreRepository.findByGenre(GenreEnum.SCIENCE).orElseThrow(EntityNotFoundException::new);
        Genre fantasy = genreRepository.findByGenre(GenreEnum.FANTASY).orElseThrow(EntityNotFoundException::new);
        Genre mystery = genreRepository.findByGenre(GenreEnum.MYSTERY).orElseThrow(EntityNotFoundException::new);

        Author robertCMartin = authorRepository.findByFullName("Robert C. Martin")
                .stream()
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        Author laurentiuSpilca = authorRepository.findByFullName("Laurentiu Spilca")
                .stream()
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        Author jkRowling = authorRepository.findByFullName("J.K. Rowling")
                .stream()
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        Publisher pearson = publisherRepository.findByName("Pearson").orElseThrow(EntityNotFoundException::new);
        Publisher manning = publisherRepository.findByName("Manning").orElseThrow(EntityNotFoundException::new);
        Publisher bloomsbury = publisherRepository.findByName("Bloomsbury").orElseThrow(EntityNotFoundException::new);

        User user = userRepository.findUserByUsername("user").orElseThrow(EntityNotFoundException::new);

        Book cleanCode = new Book();
        cleanCode.setTitle("Clean Code: A Handbook of Agile Software Craftsmanship");
        cleanCode.setAuthors(Set.of(robertCMartin));
        cleanCode.setPublisher(pearson);
        cleanCode.setGenres(Set.of(science));

        Book springSecurityInAction = new Book();
        springSecurityInAction.setTitle("Spring Security in Action");
        springSecurityInAction.setAuthors(Set.of(laurentiuSpilca));
        springSecurityInAction.setPublisher(manning);
        springSecurityInAction.setGenres(Set.of(science));

        Book springSecurityInAction2 = new Book();
        springSecurityInAction2.setTitle("Spring Security in Action, Second Edition");
        springSecurityInAction2.setAuthors(Set.of(laurentiuSpilca));
        springSecurityInAction2.setPublisher(manning);
        springSecurityInAction2.setGenres(Set.of(science));

        Book springStartHere = new Book();
        springStartHere.setTitle("Spring Start Here: Learn what you need and learn it well");
        springStartHere.setAuthors(Set.of(laurentiuSpilca));
        springStartHere.setPublisher(manning);
        springStartHere.setGenres(Set.of(science));

        Book harryPotter1 = new Book();
        harryPotter1.setTitle("Harry Potter and the Philosopher's Stone");
        harryPotter1.setAuthors(Set.of(jkRowling));
        harryPotter1.setPublisher(bloomsbury);
        harryPotter1.setGenres(Set.of(fantasy, mystery));

        Book harryPotter2 = new Book();
        harryPotter2.setTitle("Harry Potter and the Chamber of Secrets");
        harryPotter2.setAuthors(Set.of(jkRowling));
        harryPotter2.setPublisher(bloomsbury);
        harryPotter2.setGenres(Set.of(fantasy, mystery));

        Book harryPotter3 = new Book();
        harryPotter3.setTitle("Harry Potter and the prisoner of Azkaban");
        harryPotter3.setAuthors(Set.of(jkRowling));
        harryPotter3.setPublisher(bloomsbury);
        harryPotter3.setGenres(Set.of(fantasy, mystery));

        user.getBooksRead()
                .addAll(Set.of(
                        cleanCode,
                        springSecurityInAction,
                        springSecurityInAction2,
                        springStartHere,
                        harryPotter1,
                        harryPotter2,
                        harryPotter3
                ));

        bookRepository.saveAll(Set.of(
                cleanCode,
                springSecurityInAction,
                springSecurityInAction2,
                springStartHere,
                harryPotter1,
                harryPotter2,
                harryPotter3
        ));
    }
}
