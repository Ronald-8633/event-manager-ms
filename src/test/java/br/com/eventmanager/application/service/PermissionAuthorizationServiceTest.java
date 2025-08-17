package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.domain.Event;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.security.Permission;
import br.com.eventmanager.domain.security.RolePermissionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Set;

import static br.com.eventmanager.shared.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionAuthorizationServiceTest {

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private UserService userService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private PermissionAuthorizationService permissionAuthorizationService;

    private User adminUser;
    private User organizerUser;
    private User regularUser;
    private Event draftEvent;
    private Event publishedEvent;
    private Event cancelledEvent;

    @BeforeEach
    void setup() {
        adminUser = User.builder()
                .id("1")
                .email("admin@test.com")
                .role(User.UserRole.ADMIN)
                .organizedEvents(Set.of())
                .build();

        organizerUser = User.builder()
                .id("2")
                .email("org@test.com")
                .role(User.UserRole.ORGANIZER)
                .organizedEvents(Set.of("evt-1"))
                .build();

        regularUser = User.builder()
                .id("3")
                .email("user@test.com")
                .role(User.UserRole.USER)
                .organizedEvents(Set.of())
                .build();

        draftEvent = Event.builder()
                .id("evt-1")
                .title("Draft Event")
                .status(Event.EventStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build();

        publishedEvent = Event.builder()
                .id("evt-1")
                .title("Published Event")
                .status(Event.EventStatus.PUBLISHED)
                .build();

        cancelledEvent = Event.builder()
                .id("evt-1")
                .title("Cancelled Event")
                .status(Event.EventStatus.CANCELLED)
                .build();
    }

    private void mockAuth(String email, User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(email, null)
        );
        when(userService.findByEmail(email)).thenReturn(user);
    }

    @Test
    void givenUserWithPermission_whenValidatePermission_thenPasses() {
        mockAuth("admin@test.com", adminUser);
        when(rolePermissionMapper.hasPermission(adminUser.getRole(), Permission.EVENT_READ))
                .thenReturn(true);

        permissionAuthorizationService.validatePermission(Permission.EVENT_READ);

        verify(rolePermissionMapper).hasPermission(adminUser.getRole(), Permission.EVENT_READ);
    }

    @Test
    void givenUserWithoutPermission_whenValidatePermission_thenThrows() {
        mockAuth("user@test.com", regularUser);
        when(rolePermissionMapper.hasPermission(regularUser.getRole(), Permission.EVENT_DELETE))
                .thenReturn(false);
        when(messageService.getMessage(EM_0016, "EVENT_DELETE"))
                .thenReturn("Permission denied");

        assertThatThrownBy(() -> permissionAuthorizationService.validatePermission(Permission.EVENT_DELETE))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Permission denied");
    }

    @Test
    void givenAdminUser_whenValidateEventVisibility_thenAlwaysPasses() {
        mockAuth("admin@test.com", adminUser);

        permissionAuthorizationService.validateEventVisibility(draftEvent);
        permissionAuthorizationService.validateEventVisibility(cancelledEvent);
    }

    @Test
    void givenOrganizerWithOwnDraftEvent_whenValidateEventVisibility_thenPasses() {
        mockAuth("org@test.com", organizerUser);

        permissionAuthorizationService.validateEventVisibility(draftEvent);
    }

    @Test
    void givenOrganizerWithoutDraftOwnership_whenValidateEventVisibility_thenThrows() {
        mockAuth("org@test.com", organizerUser);
        Event draftNotOwned = Event.builder()
                .id("evt-2")
                .status(Event.EventStatus.DRAFT)
                .build();

        when(messageService.getMessage(EM_0017)).thenReturn("Draft not visible");

        assertThatThrownBy(() -> permissionAuthorizationService.validateEventVisibility(draftNotOwned))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Draft not visible");
    }

    @Test
    void givenAnyUser_whenCancelledEventVisibility_thenThrows() {
        mockAuth("user@test.com", regularUser);
        when(messageService.getMessage(EM_0018)).thenReturn("Cancelled event not visible");

        assertThatThrownBy(() -> permissionAuthorizationService.validateEventVisibility(cancelledEvent))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cancelled event not visible");
    }

    @Test
    void whenGetCurrentUser_thenReturnUserFromContext() {
        mockAuth("admin@test.com", adminUser);

        User result = permissionAuthorizationService.getCurrentUser();

        verify(userService).findByEmail("admin@test.com");
        assert result.equals(adminUser);
    }

    @Test
    void givenAdminUser_whenValidateEventModification_thenPasses() {
        mockAuth("admin@test.com", adminUser);

        permissionAuthorizationService.validateEventModification(publishedEvent);
    }

    @Test
    void givenOrganizerWithOwnership_whenValidateEventModificationOnDraft_thenPasses() {
        mockAuth("org@test.com", organizerUser);

        permissionAuthorizationService.validateEventModification(draftEvent);
    }

    @Test
    void givenOrganizerWithoutOwnership_whenValidateEventModification_thenThrows() {
        mockAuth("org@test.com", organizerUser);
        Event notOwned = Event.builder().id("evt-99").status(Event.EventStatus.DRAFT).build();
        when(messageService.getMessage(EM_0019)).thenReturn("Not organizer of this event");

        assertThatThrownBy(() -> permissionAuthorizationService.validateEventModification(notOwned))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Not organizer of this event");
    }

    @Test
    void givenOrganizerWithPublishedEvent_whenValidateEventModification_thenThrows() {
        mockAuth("org@test.com", organizerUser);
        when(messageService.getMessage(EM_0020)).thenReturn("Cannot modify published event");

        assertThatThrownBy(() -> permissionAuthorizationService.validateEventModification(publishedEvent))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cannot modify published event");
    }

    @Test
    void givenRegularUser_whenValidateEventModification_thenThrows() {
        mockAuth("user@test.com", regularUser);
        when(messageService.getMessage(EM_0021)).thenReturn("Users cannot modify events");

        assertThatThrownBy(() -> permissionAuthorizationService.validateEventModification(draftEvent))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Users cannot modify events");
    }
}