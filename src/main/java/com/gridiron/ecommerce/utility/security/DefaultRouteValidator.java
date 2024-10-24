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
                    Pattern.compile("/api/v1/products/public($|\\?.)")
            );

    //list of access points accessible to only ADMIN users
    public static final List<Pattern> adminAccessibleApiEndpoints =

            List.of(
                    Pattern.compile("/api/v1/users/private/admin.*"),
                    Pattern.compile("/api/v1/users/private/admin($|\\?.)"),
                    Pattern.compile("/api/v1/products/private.*"),
                    Pattern.compile("/api/v1/products/private($|\\?.)"),
                    Pattern.compile("/api/v1/orders/admin.*"),
                    Pattern.compile("/api/v1/orders/admin($|\\?.)")

            );

    //list of access points accessible to only CUSTOMER users
    public static final List<Pattern> customerAccessibleApiEndpoints =

            List.of(
                    Pattern.compile("/api/v1/carts/private.*"),
                    Pattern.compile("/api/v1/carts/private($|\\?.)"),
                    Pattern.compile("/api/v1/orders/customer.*"),
                    Pattern.compile("/api/v1/orders/customer($|\\?.)")
            );

    public Predicate<HttpServletRequest> isAdminSecured =
            request -> adminAccessibleApiEndpoints
                    .stream()
                    .anyMatch(pattern -> pattern.matcher(request.getRequestURI()).matches());

    public Predicate<HttpServletRequest> isCustomerSecured =
            request -> customerAccessibleApiEndpoints
                    .stream()
                    .anyMatch(pattern -> pattern.matcher(request.getRequestURI()).matches());

    public Predicate<HttpServletRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(pattern -> pattern.matcher(request.getRequestURI()).matches());
}
