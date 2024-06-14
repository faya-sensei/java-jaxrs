package org.faya.sensei.services;

import com.auth0.jwt.algorithms.Algorithm;
import jakarta.inject.Inject;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.payloads.UserPrincipal;
import org.faya.sensei.repositories.IRepository;

import java.util.Optional;

public class AuthService implements IAuthService {

    private static final Algorithm algorithm = Algorithm.HMAC256(System.getProperty("app.secretKey", "java-jaxrs"));
    private static final String issuer = "org.faya.sensei.java-jaxrs";

    @Inject
    private IRepository<UserEntity> userRepository;

    @Override
    public Optional<String> register(final UserDTO userDTO) {
        return Optional.empty();
    }

    @Override
    public Optional<String> login(final UserDTO userDTO) {
        return Optional.empty();
    }

    @Override
    public Optional<String> generateToken(final String id, final String name) {
        return Optional.empty();
    }

    @Override
    public Optional<UserPrincipal> parseToken(String token) {
        return Optional.empty();
    }
}
