package io.github.nerfi58.bookster.entities;

import io.github.nerfi58.bookster.entities.enums.GenreEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "genre")
@Getter
@Setter
@ToString
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_id_gen")
    @SequenceGenerator(name = "genre_id_gen", sequenceName = "genre_id_seq", allocationSize = 10)
    @Column(name = "id", nullable = false, updatable = false, unique = true, columnDefinition = "BIGINT")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", unique = true, nullable = false, columnDefinition = "VARCHAR(32)")
    private GenreEnum genre;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Book> books;
}
