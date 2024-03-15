package io.github.nerfi58.bookster.config;

import io.github.nerfi58.bookster.entities.Role;
import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.entities.enums.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;
import java.util.List;

@TestConfiguration
public class SecurityTestConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {

        Role userRole = new Role();
        Role moderatorRole = new Role();
        Role adminRole = new Role();

        userRole.setRole(RoleEnum.USER);
        moderatorRole.setRole(RoleEnum.MODERATOR);
        adminRole.setRole(RoleEnum.ADMIN);

        User userActive = new User();
        userActive.setUsername("userActive");
        userActive.setPasshash(passwordEncoder.encode("password"));
        userActive.setActive(true);
        userActive.setRoles(List.of(userRole));

        User userNotActive = new User();
        userNotActive.setUsername("userNotActive");
        userNotActive.setPasshash(passwordEncoder.encode("password"));
        userNotActive.setActive(false);
        userNotActive.setRoles(List.of(userRole));

        List<UserDetails> users = Arrays.asList(userActive, userNotActive);
        return new InMemoryUserDetailsManager(users);
    }
}
