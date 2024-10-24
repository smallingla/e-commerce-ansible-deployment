package com.gridiron.ecommerce.cart.response;

import com.gridiron.ecommerce.cartItem.response.CartItemResponse;

import java.util.List;

public record CartResponse(

        String cartId,
        List<CartItemResponse> cartItemResponse
) {
}
