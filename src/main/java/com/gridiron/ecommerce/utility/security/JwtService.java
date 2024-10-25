package com.gridiron.ecommerce.utility.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@Log
public class JwtService {

    @Value("${application.security.secretKey}")
    public String secretKey;

    /**
     * Convert the secret key to a Key object for signing the JWT.
     * This field holds the signing key for HMAC-SHA256 algorithm.
     */
    public Key getSigningKey(String secretKey) {
        return  new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    /**
     * Extracts a specific claim from the JWT token by applying a resolver function.
     *
     * @param <T> The type of the claim being resolved.
     * @param token The JWT token to extract the claim from.
     * @param claimsResolver A function to resolve the desired claim from the Claims object.
     * @return The value of the claim being resolved.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token to parse and extract claims from.
     * @return A Claims object containing all claims from the JWT token.
     */
    private Claims extractAllClaims(String token) {
        if (token.startsWith("Bearer ")) {
            token =  token.substring(7); // Remove "Bearer " prefix to get the token
        }
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a JWT token for the provided username and custom claims.
     *
     * @param username The subject (typically username) for which the token is generated.
     * @param claims Custom claims to be included in the JWT token.
     * @return A JWT token string.
     */
    public String generateToken(String username, Map<String,Object> claims){
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token The JWT token to check for expiration.
     * @return True if the token has expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(token);

            return !isTokenExpired(token);
        } catch (Exception e) {
            log.info("JWT FAILED: " + e);
            return false;
        }
    }

    public Object extractUserRole(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("role");
    }

    public Long extractUserId(String token){
        Claims claims = extractAllClaims(token);
        return Long.valueOf(claims.get("userId").toString());
    }


}
