package org.faya.sensei.services;

import org.faya.sensei.payloads.UserDTO;
import org.faya.sensei.payloads.UserPrincipal;

import java.util.Map;
import java.util.Optional;

public interface IAuthService extends IService<UserDTO> {
    /**
     * Authenticates a user from repository and retrieve the jwt token.
     *
     * @param userDTO The user data transfer object.
     * @return The nullable authentication token.
     */
    Optional<UserDTO> login(final UserDTO userDTO);

    /**
     * Generate token Based on user info.
     *
     * @param id The id of the user for subject.
     * @param payload The payload of the user for claim.
     * @return The nullable authentication token.
     */
    Optional<String> generateToken(final int id, final Map<String, String> payload);

    /**
     * Retrieve token claims and verify it base on repository.
     *
     * @param token The jwt bearer token.
     * @return The resolved user payload.
     */
    Optional<UserPrincipal> resolveToken(final String token);
}
