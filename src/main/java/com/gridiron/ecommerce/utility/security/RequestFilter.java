package com.gridiron.ecommerce.utility.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridiron.ecommerce.user.Role;
import com.gridiron.ecommerce.utility.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Log
public class RequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final DefaultRouteValidator routeValidator;


    @Value("${application.security.apiKey}")
    private String apiKey;

    public RequestFilter(JwtService jwtService, DefaultRouteValidator routeValidator) {
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {

        try {

            // Check if API Key is missing or invalid
            if (isApiKeyMissing(request)) {
                // If API key is invalid, set HTTP status and return error response
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                writeErrorResponse(response, "Invalid API Key");
                return; // Return early to avoid further processing
            }

            //Check if the route is secured and requires JWT validation
            if (routeValidator.isSecured.test(request)) {
                checkIfRequestIsAccessibleByUser(request,response);
                filterChain.doFilter(request, response);
                return;
            }

            // If no security validation is needed, continue with the filter chain
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // In case of an internal error, return a 500 status code and error message
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            writeErrorResponse(response, "An error occurred: " + e.getMessage());
        }
    }


    /**
     * This method checks if the request is accessible to users based on the roles
     * @param request current HttpServletRequest of the request to be checked
     * @param response current HttpServletResponse of the request
     * @throws IOException if any error occurs during I/0
     */

    private void checkIfRequestIsAccessibleByUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String jwtToken = extractJwtToken(request);

        if (jwtToken == null || !jwtService.isTokenValid(jwtToken)) {
            updateResponse(response, "Invalid or missing JWT", UNAUTHORIZED);
            return; // Ensure to exit the method after sending an error response
        }

        List<String> roleStrings = (List<String>) jwtService.extractUserRole(jwtToken);

        List<GrantedAuthority> authorities = roleStrings.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Prefix with 'ROLE_' if using hasRole() in SecurityConfig
                .collect(Collectors.toList());

        // Create an Authentication object and set it in the SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }


    /**
     * This method updates the response of the incoming request
     * @param response current HttpServletResponse of the request
     * @param message The message to be updated in the ApiResponse
     * @param httpStatus The Http status code to the updated to
     * @throws IOException if any error occurs during I/0
     */
    private static void updateResponse(HttpServletResponse response, String message, HttpStatus httpStatus)
            throws IOException  {

        response.setStatus(httpStatus.value());
        response.setContentType(APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(),new ApiResponse(false, message, null));
    }


    /**
     * Extracts the JWT token from the Authorization header of the request.
     *
     * @param request The HttpServletRequest object.
     * @return The JWT token if present, or null if it's missing.
     */
    private String extractJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix to get the token
        }
        return null;
    }

    /**
     * Checks if the API key is missing from the request headers.
     *
     * @param request The HttpServletRequest object.
     * @return True if the API key is missing or invalid, false otherwise.
     */
    private boolean isApiKeyMissing(HttpServletRequest request) {
        String extractedApiKey = request.getHeader("X-Api-Key");

        System.out.println("extracted api key: " + extractedApiKey);
        return extractedApiKey == null || !extractedApiKey.equals(apiKey);
    }

    /**
     * Writes an error response in JSON format to the client.
     *
     * @param response     The HttpServletResponse object.
     * @param errorMessage The error message to include in the response.
     * @throws IOException If an input or output exception occurs.
     */
    private void writeErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("application/json");
        ApiResponse apiResponse = new ApiResponse(false, errorMessage, null);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
