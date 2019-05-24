package de.greenblood.tsbot.restservice;

import de.greenblood.tsbot.database.Users;
import de.greenblood.tsbot.database.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.xml.ws.Response;
import java.util.List;

@RestController
@Component
@RequestMapping("/users")
public class BotController {
    @Autowired
    UsersRepository usersRepository;


    //@RequestMapping(path = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    Iterable<Users> helloWorld() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
            return usersRepository.findAll();
        } else {
            throw new AccessDeniedException("access denied");
        }
    }

    //@RequestMapping(path = "/users", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE)
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Object> addUser() {
        Users user=null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
            if (usersRepository.findById(user.getUsername()) == null) {
                usersRepository.save(user);
            } else {
                throw new UserAlreadyExistsException("user " + user.getUsername() + " does already exist");
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(path = "/users", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> deleteUser(Users user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("adminrole"))) {
            if (usersRepository.findById(user.getUsername()) != null) {
                usersRepository.delete(user);
            } else {
                throw new UserNotFoundException("user " + user.getUsername() + " not found");
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}