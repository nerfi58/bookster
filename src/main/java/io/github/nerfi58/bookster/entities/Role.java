package io.github.nerfi58.bookster.entities;

import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_gen")
    @SequenceGenerator(name = "role_id_gen", sequenceName = "role_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "BIGINT")
    private long id;

    @Column(name = "role", updatable = false, nullable = false, unique = true, columnDefinition = "VARCHAR(16)")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<User> users = new HashSet<>();
}
