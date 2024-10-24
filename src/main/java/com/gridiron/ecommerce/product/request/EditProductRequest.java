package com.gridiron.ecommerce.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EditProductRequest(
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "price is required") BigDecimal price,
        @NotBlank(message = "description is required") String description,
        @NotNull(message = "quantity is required") int availabilityQuantity

) {
}
