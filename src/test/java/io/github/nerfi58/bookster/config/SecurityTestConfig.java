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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        userActive.setId(1);
        userActive.setUsername("userActive");
        userActive.setPasshash(passwordEncoder.encode("password"));
        userActive.setActive(true);
        userActive.setRoles(Set.of(userRole));

        User userNotActive = new User();
        userNotActive.setId(2);
        userNotActive.setUsername("userNotActive");
        userNotActive.setPasshash(passwordEncoder.encode("password"));
        userNotActive.setActive(false);
        userNotActive.setRoles(Set.of(userRole));

        List<UserDetails> users = Arrays.asList(userActive, userNotActive);

        return new UserDetailsManager() {

            private final InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(users);

            @Override
            public void createUser(UserDetails user) {
                inMemoryUserDetailsManager.createUser(user);
            }

            @Override
            public void updateUser(UserDetails user) {
                inMemoryUserDetailsManager.updateUser(user);
            }

            @Override
            public void deleteUser(String username) {
                inMemoryUserDetailsManager.deleteUser(username);
            }

            @Override
            public void changePassword(String oldPassword, String newPassword) {
                inMemoryUserDetailsManager.changePassword(oldPassword, newPassword);
            }

            @Override
            public boolean userExists(String username) {
                return inMemoryUserDetailsManager.userExists(username);
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                for (UserDetails user : users) {
                    if (user.getUsername().equalsIgnoreCase(username)) {
                        return user;
                    }
                }
                throw new UsernameNotFoundException(username);
            }
        };
    }
}
