package dev.jjerome.qoq.test.app.application.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.jjerome.qoq.test.app.common.library.security.AuthenticatedIdentityAccessUser;
import dev.jjerome.qoq.test.app.common.library.security.IdentityAccessUser;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;

import static java.util.Objects.isNull;

@Service
public class JwtService {
    @Value("${app.security.secret:AppSecretKey}")
    private String jwtSecret;
    @Value("${app.security.lifetime:172800}")
    private int lifetime;
    @Value("${app.security.starts:Bearer}")
    private String tokenStarts;

    private JWTVerifier verifier;

    @PostConstruct
    private void init() {
        verifier = JWT.require(getSignKey()).build();
    }

    public String generateToken(IdentityAccessUser user) {
        return JWT.create()
                .withSubject(user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("email", user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(lifetime))
                .sign(getSignKey());
    }

    public IdentityAccessUser getUserDetails(DecodedJWT decodedJWT) {
        String id = decodedJWT.getSubject();
        String username = decodedJWT.getClaim("username").asString();
        String email = decodedJWT.getClaim("email").asString();
        return new AuthenticatedIdentityAccessUser(id, username, email);
    }

    @Transactional(readOnly = true)
    public void authenticate(String header) {
        DecodedJWT decodedJWT = verifyToken(header);

        if (isNull(decodedJWT)) {
            return;
        }

        IdentityAccessUser userDetails = getUserDetails(decodedJWT);

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>()));
    }

    public DecodedJWT verifyToken(String header) {
        if (StringUtils.isBlank(header) || !StringUtils.startsWith(header, tokenStarts)) {
            return null;
        }

        String token = StringUtils.removeStart(header, tokenStarts).trim();
        DecodedJWT decodedJWT = verifier.verify(token);

        if (decodedJWT.getExpiresAtAsInstant().isBefore(Instant.now())) {
            throw new IllegalStateException("Expired or invalid token");
        }

        return decodedJWT;
    }

    private Algorithm getSignKey() {
        return Algorithm.HMAC256(jwtSecret);
    }
}
