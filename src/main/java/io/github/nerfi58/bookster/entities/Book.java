package io.github.nerfi58.bookster.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "book")
@Getter
@Setter
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_id_gen")
    @SequenceGenerator(name = "book_id_gen", sequenceName = "book_id_seq")
    @Column(name = "id", nullable = false, updatable = false, unique = true, columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(256)")
    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id", columnDefinition = "BIGINT"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id",
                    columnDefinition = "BIGINT"))
    @OrderBy("genre")
    private Set<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id", columnDefinition = "BIGINT"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id",
                    columnDefinition = "BIGINT"))
    private Set<Author> authors;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false, columnDefinition = "BIGINT")
    private Publisher publisher;

    @ManyToMany(mappedBy = "booksRead", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<User> usersWhoRead;
}
