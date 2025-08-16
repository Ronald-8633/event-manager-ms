package br.com.eventmanager.adapter.inbound.rest;

import br.com.eventmanager.application.service.AuthService;
import br.com.eventmanager.domain.dto.AuthResponseDTO;
import br.com.eventmanager.domain.dto.LoginRequestDTO;
import br.com.eventmanager.domain.dto.RegisterRequestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Received registration request for email: {}", request.getEmail());

        try {
            AuthResponseDTO response = authService.register(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Received login request for email: {}", request.getEmail());

        try {
            AuthResponseDTO response = authService.login(request);
            log.info("User logged in successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error logging in user: {}", e.getMessage(), e);
            throw e;
        }
    }
}
