package com.gridiron.ecommerce.utility.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class DefaultRouteValidator {

    //list of access points accessible to unauthenticated users
    public static final List<Pattern> openApiEndpoints =
            List.of(Pattern.compile("/api/v1/users/public.*"),
                    Pattern.compile("/api/v1/users/public($|\\?.)"),
                    Pattern.compile("/api/v1/products/public.*"),
                    Pattern.compile("/api/v1/products/public($|\\?.)"),
                    Pattern.compile("/api/v1/actuator($|\\?.)"),
                    Pattern.compile("/api/v1/actuator.*")
            );

    public Predicate<HttpServletRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(pattern -> pattern.matcher(request.getRequestURI()).matches());
}
