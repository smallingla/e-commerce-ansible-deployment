package com.gridiron.ecommerce.cartItem.request;

import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequest(

        @NotNull(message = "productId is required") Long productId,
        @NotNull(message = "quantity is required") int quantity
) {
}
