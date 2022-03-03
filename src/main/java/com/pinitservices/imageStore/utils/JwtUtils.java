package com.pinitservices.imageStore.utils;

import com.pinitservices.imageStore.model.Role;
import com.pinitservices.imageStore.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    public static final String SECRET = "TcwbewNUck4rUkw23JUUSSAe7ryrCJXwCfvLZS84+Vg4Or1WWYDY6kM430aJOvU1KAQQKSs2xFd9ImCE+hocyA==";


    public String createToken(User user) {
        var now = Instant.now();

        var hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SECRET), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder().setClaims(user.toMap()).setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(7, ChronoUnit.DAYS))).signWith(hmacKey).compact();
    }


    public User parse(String token) {

        final var jwt = Jwts.parserBuilder().setSigningKey(SECRET).build().parse(token);

        var claims = (Claims) jwt.getBody();

        return User.builder().userId((String) claims.get("id"))
                .roles(((List<String>) claims.get("roles")).stream().map(Role::valueOf).toList()).build();
    }

}
