package io.github.nerfi58.bookster.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_token")
@Getter
@Setter
@ToString
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confirmation_token_gen")
    @SequenceGenerator(name = "user_confirmation_token_gen", sequenceName = "confirmation_token_seq",
            allocationSize = 10)
    private long id;

    @Column(name = "token", nullable = false, unique = true, updatable = false, columnDefinition = "VARCHAR(36)")
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false, columnDefinition = "BIGINT")
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime confirmedAt;
}
