package org.faya.sensei.services;

import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.payloads.UserPrincipal;

import java.util.Optional;

public interface IAuthService {
    /**
     * Register a user into repository and retrieve the jwt token.
     *
     * @param userDTO The user data transfer object.
     * @return The nullable authentication token.
     */
    Optional<String> register(final UserDTO userDTO);

    /**
     * Authenticates a user from repository and retrieve the jwt token.
     *
     * @param userDTO The user data transfer object.
     * @return The nullable authentication token.
     */
    Optional<String> login(final UserDTO userDTO);

    /**
     * Generate token Based on user info.
     *
     * @param id The id of the user for subject.
     * @param name The name of the user.
     * @return The nullable authentication token.
     */
    Optional<String> generateToken(final String id, final String name);

    /**
     * Retrieve token claims and verify it base on repository.
     *
     * @param token The jwt bearer token.
     * @return The resolved user payload.
     */
    Optional<UserPrincipal> parseToken(final String token);
}
