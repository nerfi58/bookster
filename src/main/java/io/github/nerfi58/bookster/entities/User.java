package io.github.nerfi58.bookster.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_gen")
    @SequenceGenerator(name = "user_id_gen", sequenceName = "user_id_seq", allocationSize = 10)
    @Column(name = "id", nullable = false, unique = true, updatable = false, columnDefinition = "BIGINT")
    private long id;

    @Column(name = "username", nullable = false, unique = true, updatable = false, columnDefinition = "VARCHAR(32)")
    private String username;

    @Column(name = "passhash", nullable = false, columnDefinition = "VARCHAR(60)")
    private String passhash;

    @Column(name = "email", nullable = false, columnDefinition = "VARCHAR(72)")
    private String email;

    @Column(name = "created", nullable = false, updatable = false, columnDefinition = "DATE")
    private Date created;

    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN")
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();
}
