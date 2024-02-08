package io.github.nerfi58.bookster.security;

import io.github.nerfi58.bookster.entities.User;
import io.github.nerfi58.bookster.mappers.UserMapper;
import io.github.nerfi58.bookster.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsJpaService implements UserDetailsService {
    
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsJpaService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("username");
        Optional<User> user = userRepository.findByUsername(username);
        System.out.println(user.toString());
        return UserMapper.userToUserDetails(user.orElseThrow(() -> new UsernameNotFoundException("Username not found")));
    }
}
