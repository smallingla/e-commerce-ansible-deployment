package com.gridiron.ecommerce.user.request;

import jakarta.validation.constraints.NotBlank;

public record AuthenticateUserRequest(
        @NotBlank(message = "last name is required") String email,
        @NotBlank(message = "last name is required") String password
) {
}
