package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.UserRepository;
import br.com.eventmanager.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static br.com.eventmanager.shared.Constants.EM_0022;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id("123")
                .name("John Doe")
                .email("john.doe@test.com")
                .organizedEvents(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void givenExistingEmail_whenFindByEmail_thenReturnUser() {
        when(userRepository.findByEmail("john.doe@test.com"))
                .thenReturn(Optional.of(user));

        User result = userService.findByEmail("john.doe@test.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@test.com");
        verify(userRepository).findByEmail("john.doe@test.com");
    }

    @Test
    void givenNonExistingEmail_whenFindByEmail_thenThrowBusinessException() {
        when(userRepository.findByEmail("notfound@test.com"))
                .thenReturn(Optional.empty());
        when(messageService.getMessage(EM_0022, "notfound@test.com"))
                .thenReturn("User not found");

        assertThatThrownBy(() -> userService.findByEmail("notfound@test.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found");

        verify(userRepository).findByEmail("notfound@test.com");
    }

    @Test
    void givenId_whenFindById_thenReturnUserOptional() {
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById("123");

        assertThat(result).isPresent().contains(user);
        verify(userRepository).findById("123");
    }

    @Test
    void whenFindAll_thenReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1).contains(user);
        verify(userRepository).findAll();
    }

    @Test
    void givenExistingUserId_whenDeleteUser_thenDeleteSuccessfully() {
        when(userRepository.existsById("123")).thenReturn(true);

        userService.deleteUser("123");

        verify(userRepository).deleteById("123");
    }

    @Test
    void givenNonExistingUserId_whenDeleteUser_thenThrowBusinessException() {
        when(userRepository.existsById("456")).thenReturn(false);
        when(messageService.getMessage(EM_0022, "456"))
                .thenReturn("User not found");

        assertThatThrownBy(() -> userService.deleteUser("456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    void givenUser_whenAddOrganizedEvent_thenEventIsAdded() {
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        userService.addOrganizedEvent("123", "event-1");

        assertThat(user.getOrganizedEvents()).contains("event-1");
        assertThat(user.getUpdatedAt()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void givenNonExistingUser_whenAddOrganizedEvent_thenThrowBusinessException() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());
        when(messageService.getMessage(EM_0022, "999"))
                .thenReturn("User not found");

        assertThatThrownBy(() -> userService.addOrganizedEvent("999", "event-1"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void givenUserWithEvent_whenRemoveOrganizedEvent_thenEventIsRemoved() {
        user.getOrganizedEvents().add("event-1");
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        userService.removeOrganizedEvent("123", "event-1");

        assertThat(user.getOrganizedEvents()).doesNotContain("event-1");
        assertThat(user.getUpdatedAt()).isNotNull();
        verify(userRepository).save(user);
    }

    @Test
    void givenUserWithoutEvent_whenRemoveOrganizedEvent_thenDoNothing() {
        when(userRepository.findById("123")).thenReturn(Optional.of(user));

        userService.removeOrganizedEvent("123", "event-1");

        assertThat(user.getOrganizedEvents()).doesNotContain("event-1");
        verify(userRepository).save(user);
    }

    @Test
    void givenNonExistingUser_whenRemoveOrganizedEvent_thenThrowBusinessException() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());
        when(messageService.getMessage(EM_0022, "999"))
                .thenReturn("User not found");

        assertThatThrownBy(() -> userService.removeOrganizedEvent("999", "event-1"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).save(any());
    }
}
