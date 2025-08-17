package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.UserRepository;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.dto.AuthResponseDTO;
import br.com.eventmanager.domain.dto.LoginRequestDTO;
import br.com.eventmanager.domain.dto.RegisterRequestDTO;
import br.com.eventmanager.domain.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .email("test@example.com")
                .name("Test User")
                .password("password")
                .phone("123456")
                .role(User.UserRole.USER)
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("token");

        AuthResponseDTO response = authService.register(request);

        assertEquals("token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("exists@example.com");

        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);
        when(messageService.getMessage(anyString())).thenReturn("Email exists");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.register(request));
        assertEquals("Email exists", exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("user@example.com");
        request.setPassword("password");

        User user = User.builder()
                .email("user@example.com")
                .name("User")
                .role(User.UserRole.USER)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(org.springframework.security.core.Authentication.class));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserPrincipal.class))).thenReturn("token");

        AuthResponseDTO response = authService.login(request);

        assertEquals("token", response.getToken());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("User", response.getName());
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("nouser@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail("nouser@example.com")).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("User not found");

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));
        assertEquals("User not found", exception.getMessage());
    }
}
