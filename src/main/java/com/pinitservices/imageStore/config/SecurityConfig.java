
package com.pinitservices.imageStore.config;

import com.pinitservices.imageStore.model.User;
import com.pinitservices.imageStore.security.AuthenticationManager;
import com.pinitservices.imageStore.security.SecurityContextRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author Ramdane
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private SecurityContextRepo repo;

    @Bean
    public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http) {

        return http.authorizeExchange()
                .pathMatchers("/image/**", "/video/**")
                .permitAll()
                .anyExchange().authenticated()
                .and().csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .cors().disable()
                .authenticationManager(manager)
                .securityContextRepository(repo)
                .logout().disable()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Scope("prototype")
    Mono<SecurityContext> context() {
        return ReactiveSecurityContextHolder.getContext();
    }

    @Bean
    @Scope("prototype")
    Mono<User> user() {
        return context().map(sc -> (UsernamePasswordAuthenticationToken) sc.getAuthentication())
                .map(token -> (User) token.getPrincipal());
    }

}
