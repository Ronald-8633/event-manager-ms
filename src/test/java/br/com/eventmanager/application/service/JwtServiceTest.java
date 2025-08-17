package br.com.eventmanager.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "mySuperSecretKeyThatIsLongEnoughForHS256");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000L * 60 * 60);

        userDetails = User.withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void givenUserDetails_whenGenerateToken_thenTokenIsCreated() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    void givenToken_whenExtractUsername_thenReturnsCorrectUser() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void givenTokenWithExtraClaims_whenExtractClaims_thenContainsClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = jwtService.generateToken(claims, userDetails);

        String role = jwtService.extractClaim(token, (Claims c) -> c.get("role", String.class));

        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    void givenValidToken_whenIsTokenValid_thenReturnsTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void givenDifferentUser_whenIsTokenValid_thenReturnsFalse() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = User.withUsername("otheruser")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    void givenExpiredToken_whenIsTokenValid_thenReturnsFalse() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);

        String token = jwtService.generateToken(userDetails);

        Thread.sleep(5);

        try {
            jwtService.isTokenValid(token, userDetails);
        } catch (Exception e) {

            assertThat(e instanceof ExpiredJwtException);
        }

    }

    @Test
    void givenInvalidToken_whenExtractUsername_thenThrowsException() {
        String invalidToken = "invalid.token.value";

        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(Exception.class);
    }
}

