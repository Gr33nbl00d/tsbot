package de.greenblood.tsbot.restservice.security;

import de.greenblood.tsbot.database.Authorities;
import de.greenblood.tsbot.database.Users;
import de.greenblood.tsbot.database.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;


public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
        List<Authorities> authorities = user.getAuthorities();
        return new UserDetailsAdapter(user);
    }
}
