package com.gridiron.ecommerce.orderItem.response;

import com.gridiron.ecommerce.product.response.ProductResponse;

public record OrderItemResponse(
        Long orderItemId,
        ProductResponse product,
        int quantity
) {
}
