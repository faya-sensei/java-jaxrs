package org.faya.sensei.services;

import com.auth0.jwt.algorithms.Algorithm;
import jakarta.inject.Inject;
import org.faya.sensei.entities.UserEntity;
import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.payloads.UserPrincipal;
import org.faya.sensei.repositories.IRepository;

import java.util.Map;
import java.util.Optional;

public class AuthService implements IAuthService {

    private static final Algorithm algorithm = Algorithm.HMAC256(System.getProperty("app.secretKey", "java-jaxrs"));
    private static final String issuer = "org.faya.sensei.java-jaxrs";

    @Inject
    private IRepository<UserEntity> userRepository;

    @Override
    public Optional<UserDTO> login(final UserDTO dto) {
        return Optional.empty();
    }

    @Override
    public Optional<String> generateToken(int id, Map<String, String> payload) {
        return Optional.empty();
    }

    @Override
    public Optional<UserPrincipal> resolveToken(String token) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDTO> create(UserDTO dto) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDTO> update(int id, UserDTO dto) {
        return Optional.empty();
    }

    @Override
    public boolean remove(int id) {
        return false;
    }
}
