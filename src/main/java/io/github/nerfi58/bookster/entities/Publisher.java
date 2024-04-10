package io.github.nerfi58.bookster.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "publisher")
@Getter
@Setter
@ToString
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "publisher_id_gen", sequenceName = "publisher_id_seq", allocationSize = 10)
    @Column(name = "id", nullable = false, unique = true, updatable = false, columnDefinition = "BIGINT")
    private long id;

    @Column(name = "name", unique = true, nullable = false, columnDefinition = "VARCHAR(128)")
    private String name;

    @OneToMany(mappedBy = "publisher")
    private Set<Book> books;
}
