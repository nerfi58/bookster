package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
