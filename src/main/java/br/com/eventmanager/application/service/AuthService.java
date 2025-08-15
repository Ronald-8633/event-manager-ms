package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.UserRepository;
import br.com.eventmanager.domain.User;
import br.com.eventmanager.domain.dto.AuthResponseDTO;
import br.com.eventmanager.domain.dto.LoginRequestDTO;
import br.com.eventmanager.domain.dto.RegisterRequestDTO;
import br.com.eventmanager.domain.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Registering new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        String token = jwtService.generateToken(new UserPrincipal(user));

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .message("User registered successfully")
                .build();
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for user: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        String token = jwtService.generateToken(new UserPrincipal(user));
        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}