package io.github.nerfi58.bookster.mappers;

import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.security.UserDetailsJpa;
import org.springframework.security.core.GrantedAuthority;

public class UserMapper {

    public static UserDetailsJpa userToUserDetails(User user) {
        
        UserDetailsJpa userDetails = new UserDetailsJpa();
        userDetails.setUsername(user.getUsername());
        userDetails.setPasshash(user.getPasshash());
        userDetails.setActive(user.isActive());
        userDetails.setRoles(user.getRoles().stream().map((role) -> (GrantedAuthority) role::toString).toList());

        return userDetails;
    }
}
