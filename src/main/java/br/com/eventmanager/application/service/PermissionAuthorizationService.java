package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.security.Permission;
import br.com.eventmanager.domain.security.RolePermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static br.com.eventmanager.shared.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionAuthorizationService {
    private final RolePermissionMapper rolePermissionMapper;
    private final UserService userService;
    private final MessageService messageService;

    public void validatePermission(Permission permission) {
        String userEmail = getCurrentUserEmail();
        User user = userService.findByEmail(userEmail);

        if (!rolePermissionMapper.hasPermission(user.getRole(), permission)) {
            log.warn("User {} attempted to access resource requiring permission: {}", userEmail, permission);
            throw new BusinessException(messageService.getMessage(EM_0016, permission.name()));
        }
    }


    public void validateEventVisibility(Event event) {
        String userEmail = getCurrentUserEmail();
        User user = userService.findByEmail(userEmail);

        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }

        if (event.getStatus() == Event.EventStatus.DRAFT) {
            if (user.getRole() == User.UserRole.ORGANIZER &&
                    user.getOrganizedEvents().contains(event.getId())) {
                return;
            }
            throw new BusinessException(messageService.getMessage(EM_0017));
        }

        if (event.getStatus() == Event.EventStatus.CANCELLED) {
            throw new BusinessException(messageService.getMessage(EM_0018));
        }
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        String userEmail = getCurrentUserEmail();
        return userService.findByEmail(userEmail);
    }

    public void validateEventModification(Event event) {
        String userEmail = getCurrentUserEmail();
        User user = userService.findByEmail(userEmail);

        if (user.getRole() == User.UserRole.ADMIN) {
            return;
        }

        if (user.getRole() == User.UserRole.ORGANIZER) {
            if (!user.getOrganizedEvents().contains(event.getId())) {
                throw new BusinessException(messageService.getMessage(EM_0019));
            }

            if (event.getStatus() == Event.EventStatus.PUBLISHED) {
                throw new BusinessException(messageService.getMessage(EM_0020));
            }
        }

        if (user.getRole() == User.UserRole.USER) {
            throw new BusinessException(messageService.getMessage(EM_0021));
        }
    }
}
