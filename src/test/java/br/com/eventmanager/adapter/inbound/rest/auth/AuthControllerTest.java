package br.com.eventmanager.adapter.inbound.rest.auth;

import br.com.eventmanager.application.service.AuthService;
import br.com.eventmanager.domain.dto.AuthResponseDTO;
import br.com.eventmanager.domain.dto.LoginRequestDTO;
import br.com.eventmanager.domain.dto.RegisterRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setEmail("test@example.com");

        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .email("test@example.com")
                .token("token")
                .name("Test")
                .build();

        when(authService.register(request)).thenReturn(responseDTO);

        ResponseEntity<AuthResponseDTO> response = authController.register(request);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    void testLogin() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("login@example.com");

        AuthResponseDTO responseDTO = AuthResponseDTO.builder()
                .email("login@example.com")
                .token("token")
                .name("LoginUser")
                .build();

        when(authService.login(request)).thenReturn(responseDTO);

        ResponseEntity<AuthResponseDTO> response = authController.login(request);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("login@example.com", response.getBody().getEmail());
        assertEquals("token", response.getBody().getToken());
    }
}