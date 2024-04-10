package io.github.nerfi58.bookster.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@ToString
public class User implements UserDetails {

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
    private LocalDate created;

    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN")
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", columnDefinition = "BIGINT"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", columnDefinition = "BIGINT")
    )
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_read_by_user",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", columnDefinition = "BIGINT"),
            inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id", columnDefinition = "BIGINT")
    )
    private Set<Book> booksRead;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().toString())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return passhash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }
}
