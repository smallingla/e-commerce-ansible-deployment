package com.gridiron.ecommerce.utility.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridiron.ecommerce.utility.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * CustomAccessDeniedHandler handles cases where an user attempts to access a resource
 * they do not have sufficient permissions for.
 * *
 * This class implements the Spring Security AccessDeniedHandler interface and is used to send a
 * custom response whenever a 403 Forbidden error occurs.
 *  *
 * When a user is authenticated but tries to access a restricted endpoint that their roles do not permit,
 * Spring Security triggers this handler, which customizes the error response.
 *  *
 * The default behavior of Spring Security for access denial is to render a simple HTML page or basic
 * error message. By providing this custom handler, we can return a JSON response that is more suitable
 * for API-based applications, giving clients more context about the error.
 *  *
 *  This handler sets the response status to 403 Forbidden and returns a JSON object containing an
 *  error message, making it easier for frontend applications to handle authorization errors.
 */
@Configuration
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Handles the AccessDeniedException by setting a 403 status and writing a custom JSON
     * error message to the response.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object where the response is written.
     * @param accessDeniedException The exception that caused the access to be denied.
     * @throws IOException If an input or output exception occurs while writing the response.
     * @throws ServletException If a servlet-specific error occurs.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (!response.isCommitted()) {
            // Set the status code to 403 Forbidden
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

            // Create a custom error message
            ApiResponse apiResponse = new ApiResponse(false, "Access Denied", null);

            // Write the custom error message to the response
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }
    }
}
