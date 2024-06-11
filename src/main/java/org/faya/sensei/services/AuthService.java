package org.faya.sensei.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.inject.Inject;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.payloads.UserPrincipal;
import org.faya.sensei.repositories.IRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthService {

    private static final Algorithm algorithm = Algorithm.HMAC256(System.getProperty("app.secretKey", "java-jaxrs"));
    private static final String issuer = "org.faya.sensei.java-jaxrs";

    @Inject
    private IRepository<UserEntity> userRepository;

    public Optional<String> register(final UserDTO userDTO) {
        return Optional.empty();
    }

    public Optional<String> login(final UserDTO userDTO) {
        return Optional.empty();
    }

    public Optional<UserPrincipal> retrieveTokenClaims(final String token) {
        return Optional.empty();
    }

    private Optional<String> hashPassword(final String password) {
        try {
            byte[] hashedBytes = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));

            return Optional.of(IntStream.range(0, hashedBytes.length)
                    .mapToObj(i -> String.format("%02x", hashedBytes[i]))
                    .collect(Collectors.joining()));
        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }
    }

    private String generateToken(final UserEntity userEntity, final long duration) {
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(duration)))
                .sign(algorithm);
    }
}
