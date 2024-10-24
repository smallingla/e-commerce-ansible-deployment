package com.gridiron.ecommerce.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateUserRequest(

        @NotBlank(message = "first name is required") String firstName,
        @NotBlank(message = "last name is required") String lastName,
        @NotBlank(message = "email is required") @Email(message = "Email should be valid")String email,
        @NotBlank(message = "password is required") String password

) {
}
