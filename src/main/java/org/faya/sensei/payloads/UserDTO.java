package org.faya.sensei.payloads;

import org.faya.sensei.entities.UserEntity;

public final class UserDTO {

    private Integer id;

    private String name;

    private String password;

    private String role;

    private String token;

    // Converters

    public static UserEntity toEntity(final UserDTO userDTO) {
        return new UserEntity();
    }

    public static UserDTO fromEntity(final UserEntity userEntity) {
        return new UserDTO();
    }
}
