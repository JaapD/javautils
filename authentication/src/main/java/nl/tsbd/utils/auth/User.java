package nl.tsbd.utils.auth;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    @Getter
    private final String name;
    private final Map<String, Role> roles = new HashMap<>();
    @Getter
    @Setter
    private boolean admin;

    public User(String name) {
        this.name = name;
    }

    public void addRole(Role role) {
        roles.put(role.getName(), role);
    }

    public boolean hasRole(String roleName) {
        return admin || roles.containsKey(roleName);
    }

    public boolean hasRoles() {
        return !roles.isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
