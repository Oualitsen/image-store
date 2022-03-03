package com.pinitservices.imageStore.security;

import com.pinitservices.imageStore.model.Role;
import com.pinitservices.imageStore.utils.JwtUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private JwtUtils utils;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        if (authentication instanceof UsernamePasswordAuthenticationToken upat) {
            final var token = (String) upat.getName();
            var user = utils.parse(token);
            var auth = new UsernamePasswordAuthenticationToken(user, "", user.getRoles().stream().map(Role::toString)
                    .map(r -> String.format("ROLE_%s", r))
                    .map(SimpleGrantedAuthority::new).toList());

            return Mono.just(auth);

        }

        return Mono.just(authentication);
    }
}
