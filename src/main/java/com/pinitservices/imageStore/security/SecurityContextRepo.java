package com.pinitservices.imageStore.security;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log
@Component
@AllArgsConstructor
public class SecurityContextRepo implements ServerSecurityContextRepository {
    private AuthenticationManager manager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {

        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(h -> h.startsWith("Bearer ")).map(h -> h.substring(7))
                .flatMap(token -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(token, "");
                    return manager.authenticate(auth).map(SecurityContextImpl::new);
                });

    }
}
