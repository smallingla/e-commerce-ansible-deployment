package com.gridiron.ecommerce.cartItem;

import com.gridiron.ecommerce.cartItem.response.CartItemResponse;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.utility.PaginatedData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public PaginatedData fetchCartItemsByCartId(Long cartId, int page, int size) {
        Page<CartItem> cartItems = cartItemRepository.findAllByCartId(cartId, PageRequest.of(page-1, size));
        System.out.println(cartItems.getContent());
        return new PaginatedData(cartItems.getTotalPages(),
                cartItems.getNumberOfElements(),
                cartItems.getTotalElements(),
                formatCartItems(cartItems.getContent()));
    }

    List<CartItemResponse> formatCartItems(List<CartItem> cartItems) {
        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        for (CartItem cartItem : cartItems) {

            cartItemResponses.add(new CartItemResponse(
                    cartItem.getId(),
                    productService.formatProductsToProductResponse(Set.of(cartItem.getProduct())).get(0),
                    cartItem.getQuantity()
            ));
        }
        return cartItemResponses;
    }


}
