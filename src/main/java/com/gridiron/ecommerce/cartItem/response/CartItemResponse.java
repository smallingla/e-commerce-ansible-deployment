package com.gridiron.ecommerce.cartItem.response;

import com.gridiron.ecommerce.product.response.ProductResponse;

import java.util.List;

public record CartItemResponse(

        Long cartItemId,
        ProductResponse product,
        int quantity
) {
}
