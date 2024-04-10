package io.github.nerfi58.bookster.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "author")
@Getter
@Setter
@ToString
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "author_id_gen", sequenceName = "author_id_seq", allocationSize = 10)
    @Column(name = "id", nullable = false, unique = true, updatable = false, columnDefinition = "BIGINT")
    private long id;

    @Column(name = "full_name", nullable = false, columnDefinition = "VARCHAR(64)")
    private String fullName;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Book> books;
}
