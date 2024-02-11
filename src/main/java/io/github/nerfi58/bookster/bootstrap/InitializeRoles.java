package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import io.github.nerfi58.bookster.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class InitializeRoles implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public InitializeRoles(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        Role userRole = new Role();
        userRole.setRole(RoleEnum.USER);
        roleRepository.save(userRole);

        Role moderatorRole = new Role();
        moderatorRole.setRole(RoleEnum.MODERATOR);
        roleRepository.save(moderatorRole);

        Role adminRole = new Role();
        adminRole.setRole(RoleEnum.ADMIN);
        roleRepository.save(adminRole);
    }
}
