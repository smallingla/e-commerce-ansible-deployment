package com.gridiron.ecommerce.utility;

public record ApiResponse(
        boolean success,
        String message,
        Object data
) {
}
