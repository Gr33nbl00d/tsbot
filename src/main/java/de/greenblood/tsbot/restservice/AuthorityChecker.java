package de.greenblood.tsbot.restservice;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AuthorityChecker {

    public boolean hasAuthority(Authentication auth, String authorityString) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(authorityString)) {
                return true;
            }
        }
        return false;
    }

}
