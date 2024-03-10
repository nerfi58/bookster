package io.github.nerfi58.bookster.config;

import io.github.nerfi58.bookster.security.UserDetailsJpa;
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

        UserDetailsJpa userActive = new UserDetailsJpa();
        userActive.setUsername("userActive");
        userActive.setPasshash(passwordEncoder.encode("password"));
        userActive.setActive(true);
        userActive.setRoles(List.of(() -> "USER"));

        UserDetailsJpa userNotActive = new UserDetailsJpa();
        userNotActive.setUsername("userNotActive");
        userNotActive.setPasshash(passwordEncoder.encode("password"));
        userNotActive.setActive(false);
        userNotActive.setRoles(List.of(() -> "USER"));

        List<UserDetails> users = Arrays.asList(userActive, userNotActive);
        return new InMemoryUserDetailsManager(users);
    }
}
