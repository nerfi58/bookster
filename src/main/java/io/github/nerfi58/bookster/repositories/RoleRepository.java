package io.github.nerfi58.bookster.repositories;

import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByRole(RoleEnum roleEnum);
}
