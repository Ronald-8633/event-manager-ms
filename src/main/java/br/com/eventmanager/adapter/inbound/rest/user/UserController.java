package br.com.eventmanager.adapter.inbound.rest.user;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.application.service.PermissionAuthorizationService;
import br.com.eventmanager.application.service.UserService;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.dto.UserProfileDTO;
import br.com.eventmanager.domain.security.Permission;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final PermissionAuthorizationService authorizationService;

    @Override
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        User currentUser = authorizationService.getCurrentUser();
        return ResponseEntity.ok(buildUserProfileDTO(currentUser));
    }

    @Override
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable String id) {
        authorizationService.validatePermission(Permission.USER_READ);

        User user = userService.findById(id)
                .orElseThrow(() -> new BusinessException("User not found: " + id));

        return ResponseEntity.ok(buildUserProfileDTO(user));
    }

    @Override
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        authorizationService.validatePermission(Permission.USER_READ);

        List<UserProfileDTO> users = userService.findAll().stream()
                .map(this::buildUserProfileDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        authorizationService.validatePermission(Permission.USER_DELETE);

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserProfileDTO buildUserProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole())
                .status(user.getStatus())
                .interests(user.getInterests())
                .organizedEventsCount(user.getOrganizedEvents() != null ? user.getOrganizedEvents().size() : 0)
                .attendedEventsCount(user.getAttendedEvents() != null ? user.getAttendedEvents().size() : 0)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}