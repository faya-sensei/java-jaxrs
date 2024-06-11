package org.faya.sensei.payloads;

import java.security.Principal;

public class UserPrincipal implements Principal {

    private Integer id;

    private String name;

    private String role;

    // Getters and Setters

    @Override
    public String getName() {
        return name;
    }
}
