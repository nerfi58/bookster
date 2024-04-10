package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.*;
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

        Genre horror = genreRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        Genre thriller = genreRepository.findById(2L).orElseThrow(EntityNotFoundException::new);

        Author marek = authorRepository.findById(1L).orElseThrow(EntityNotFoundException::new);
        Author jan = authorRepository.findById(2L).orElseThrow(EntityNotFoundException::new);
        Author aleksander = authorRepository.findById(3L).orElseThrow(EntityNotFoundException::new);

        Publisher publisher = publisherRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        User user = userRepository.findUserByUsername("user").orElseThrow(EntityNotFoundException::new);
        User admin = userRepository.findUserByUsername("admin").orElseThrow(EntityNotFoundException::new);

        Book book1 = new Book();
        book1.setTitle("Pierdolenie cos tam bla bla bla");
        book1.setAuthors(Set.of(marek));
        book1.setPublisher(publisher);
        book1.setGenres(Set.of(horror, thriller));

        Book book2 = new Book();
        book2.setTitle("Jakas losowa ksiazka numer 2");
        book2.setAuthors(Set.of(marek, jan));
        book2.setPublisher(publisher);
        book2.setGenres(Set.of(thriller));

        Book book3 = new Book();
        book3.setTitle("Beznadziejna ale jednoczesnie najlepsza ksiazka");
        book3.setAuthors(Set.of(marek, aleksander));
        book3.setPublisher(publisher);
        book3.setGenres(Set.of(horror));

        user.getBooksRead().addAll(Set.of(book1, book2, book3));
        admin.getBooksRead().add(book3);

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
    }
}
