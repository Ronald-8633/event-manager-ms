package br.com.eventmanager.domain.security;

import br.com.eventmanager.domain.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RolePermissionMapper {

    public Set<Permission> getPermissionsForRole(User.UserRole role) {
        return switch (role) {
            case USER -> Set.of(
                    Permission.EVENT_READ
            );

            case ORGANIZER -> Set.of(
                    Permission.EVENT_CREATE,
                    Permission.EVENT_READ,
                    Permission.EVENT_UPDATE,
                    Permission.EVENT_PUBLISH,
                    Permission.EVENT_CANCEL,
                    Permission.USER_READ
            );

            case ADMIN -> Set.of(
                    Permission.EVENT_CREATE,
                    Permission.EVENT_READ,
                    Permission.EVENT_UPDATE,
                    Permission.EVENT_DELETE,
                    Permission.EVENT_PUBLISH,
                    Permission.EVENT_CANCEL,
                    Permission.USER_READ,
                    Permission.USER_UPDATE,
                    Permission.USER_DELETE,
                    Permission.USER_SUSPEND
            );
        };
    }

    public boolean hasPermission(User.UserRole role, Permission permission) {
        return getPermissionsForRole(role).contains(permission);
    }
}
