package nl.tsbd.utils.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void userHasRole() {
        User u = new User("name");
        u.addRole(new Role("role1"));
        u.addRole(new Role("role2"));

        assertThat(u.hasRole("role1")).isTrue();
        assertThat(u.hasRole("role2")).isTrue();
        assertThat(u.hasRole("none")).isFalse();
    }

    @Test
    void userIsAdmin() {
        User u = new User("name");
        u.setAdmin(true);
        u.addRole(new Role("role1"));

        assertThat(u.isAdmin()).isTrue();
        assertThat(u.hasRole("role1")).isTrue();
        assertThat(u.hasRole("none")).isTrue();
    }
}
