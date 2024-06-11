package org.faya.sensei.entities;

public enum UserRole {

    NONE,

    USER(1),

    ADMIN(2);

    private final int level;

    UserRole() {
        this.level = 0;
    }

    UserRole(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
