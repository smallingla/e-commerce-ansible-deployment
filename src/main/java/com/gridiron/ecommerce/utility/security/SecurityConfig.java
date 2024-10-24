package com.gridiron.ecommerce.utility.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final RequestFilter requestFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(RequestFilter requestFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.requestFilter = requestFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    /**
     * Configures the security filter chain, specifying how different endpoints are secured.
     * *
     * This method sets up the following:
     * - Disables CSRF protection for stateless JWT authentication.
     * - Allows unrestricted access to public endpoints like actuator endpoints and user registration APIs.
     * - Secures `api/v1/products/private/**` and `api/v1/orders/admin/**` endpoints to `ADMIN` role.
     * - Secures `api/v1/carts/private/**` and `api/v1/orders/customer/**` endpoints to `CUSTOMER` role.
     * - Ensures all other requests require authentication.
     * - Registers the custom `RequestFilter` before the `UsernamePasswordAuthenticationFilter` to process JWTs.
     * - Configures custom exception handling for access-denied scenarios using `CustomAccessDeniedHandler`.
     *
     * @param http The HttpSecurity object used to configure security settings.
     * @return A SecurityFilterChain that defines the security rules for the application.
     * @throws Exception If any error occurs during the configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if using stateless JWTs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "api/v1/users/public/**","api/v1/products/public/**").permitAll() // Allow access to actuator endpoints
                        .requestMatchers("/api/v1/products/private/**", "/api/v1/orders/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/carts/private/**","/api/v1/orders/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a custom AuthenticationManager for managing authentication.
     * *
     * This is useful when custom authentication logic is required, such as using a custom
     * UserDetailsService or other custom authentication providers.
     *
     * @param authenticationConfiguration The configuration for managing authentication.
     * @return The AuthenticationManager used by Spring Security.
     * @throws Exception If any error occurs during the creation of the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configures Spring Security to ignore specific static resource paths.
     * *
     * This allows Spring Security to bypass security checks for requests that match the specified paths,
     * improving performance for serving static resources like JavaScript, CSS, or image files.
     *
     * @return A WebSecurityCustomizer that configures paths to ignore.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/resources/**", "/static/**"); // Adjust for static resources
    }
}
