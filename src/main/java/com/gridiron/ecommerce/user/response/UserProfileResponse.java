package com.gridiron.ecommerce.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gridiron.ecommerce.user.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserProfileResponse(
        String token,
        Long userId,
        String firstName,
        String lastName,
        String emailAddress,
        Set<Role> roles
) {
}
