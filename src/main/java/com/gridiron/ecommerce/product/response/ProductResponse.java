package com.gridiron.ecommerce.product.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(

        Long productId,
        String name,
        String description,
        int availabilityQuantity,
        BigDecimal price

) {
}
