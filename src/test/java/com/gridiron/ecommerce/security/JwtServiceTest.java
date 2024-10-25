package com.gridiron.ecommerce.security;
import com.gridiron.ecommerce.utility.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String secretKey = "mySecretKeymySecretKeymySecretKey";

    @BeforeEach
    void setUp() {
        // This simulates setting the secretKey from application properties
        jwtService.secretKey = secretKey;
    }

    @Test
    void generateToken_ShouldReturnToken_WhenCalled() {
        // Arrange
        String username = "testUser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("userId", 1L);

        // Act
        String token = jwtService.generateToken(username, claims);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }



    @Test
    void extractUserId_ShouldReturnUserId_WhenTokenIsValid() {
        // Arrange
        String username = "testUser";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("userId", 1L);
        String token = jwtService.generateToken(username, claims);

        // Act
        Long userId = jwtService.extractUserId(token);

        // Assert
        assertEquals(1L, userId);
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        // Generate a token with a short expiration time
        String token = jwtService.generateToken("testUser", claims);

        // Simulate token expiration by modifying the expiration date in the token directly
        Date expiredDate = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Decode the token and manipulate its expiration date
        Claims parsedClaims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Create a new token with the modified expiration
        String expiredToken = Jwts.builder()
                .setClaims(parsedClaims)
                .setExpiration(expiredDate) // Set to past date to simulate expiration
                .signWith(jwtService.getSigningKey(secretKey))
                .compact();

        // Act
        boolean isExpired = false;
        try {
            jwtService.isTokenExpired(expiredToken);
        }catch (Exception e) {
            isExpired = true;
        }

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = jwtService.generateToken("testUser", claims);

        // Act
        boolean isValid = jwtService.isTokenValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Arrange
        String invalidToken = "invalidToken";

        // Act
        boolean isValid = jwtService.isTokenValid(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    // Helper method to get signing key
    private Key getSigningKey(String secretKey) {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}