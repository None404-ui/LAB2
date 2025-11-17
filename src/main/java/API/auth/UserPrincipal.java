package API.auth;

import java.security.Principal;

public class UserPrincipal implements Principal {
    private final String username;
    private final Role role;

    public UserPrincipal(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    @Override
    public String getName() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public boolean hasRole(Role requiredRole) {
        return this.role == requiredRole;
    }
}