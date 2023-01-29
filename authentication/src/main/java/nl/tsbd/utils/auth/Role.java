package nl.tsbd.utils.auth;

import lombok.Getter;

public class Role {
    @Getter
    private final String name;

    public Role(String name) {
        this.name = name;
    }
}
