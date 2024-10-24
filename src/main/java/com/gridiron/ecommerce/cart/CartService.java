package com.gridiron.ecommerce.cart;

import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.cartItem.CartItemService;
import com.gridiron.ecommerce.cartItem.request.CreateCartItemRequest;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemService cartItemService;

    /**
     * This method updates a cart
     * @param existingCart instance of cart to be updated
     */
    private void updateCart(Cart existingCart) {
        existingCart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(existingCart);
    }

    /**
     * This method initializes a new car for users that have no associated cart
     * @param userId The userId for cart to be created
     * @return Cart
     */
    private Cart initializeCartForUser(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    /**
     * This method adds an item to the user cart
     * @param userId The userId of the users cart
     * @param createCartItemRequest The request containing the productId, and quantity
     */
    public void addItemToCart(Long userId, CreateCartItemRequest createCartItemRequest) {

        //check to confirm products exists before adding to cart (extendable to confirm the quantity as well)
        Product product = productRepository.findById(createCartItemRequest.productId()).orElseThrow(
                () -> new ResourceNotFoundException("Product with id " + createCartItemRequest.productId() + " not found")
        );

        Optional<Cart> cart = cartRepository.findByUserId(userId);
        if(cart.isEmpty()){
            cart = Optional.of(initializeCartForUser(userId));
        }

        Optional<CartItem> existingItem = cart.get().getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(createCartItemRequest.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + createCartItemRequest.quantity());
        }else{
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart.get());
            cartItem.setProduct(product);
            cartItem.setQuantity(createCartItemRequest.quantity());
            cart.get().getCartItems().add(cartItem);
        }

        updateCart(cart.get());
    }


    /**
     * This method updates a cart, removing a product from the cart using the cartItemId
     * @param userId The userId of the cart owner
     * @param cartItemId the cartItemId of the cart.
     */
    public void deleteItemFromCart(Long userId, Long cartItemId) {
        Cart cart = findCartByUserId(userId);

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();

        if (existingItem.isPresent()) {
            cart.getCartItems().remove(existingItem.get());
            updateCart(cart);
        }else{
            throw new ResourceNotFoundException("Item with id " + cartItemId + " not found");
        }
    }

    /**
     * This method fetches the product items in a cart by the userId form the cartItemService
     * @param userId The id of the user cart to be viewed
     * @param page The page number to be viewed
     * @param size The size of the products to be viewed
     */
    public PaginatedData fetchProductInCartByCartId(Long userId, int page, int size) {

        Cart cart = findCartByUserId(userId);
        return cartItemService.fetchCartItemsByCartId(cart.getId(), page, size);
    }

    /**
     * This method fetches a cart by the userId
     * @param userId the userId of the cart to be fetched
     */
    private Cart findCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    /**
     * This method clear a user cart. It empties out all the items in the cart
     * @param cart The Cart instance to be emptied
     */
    public void clearCart(Cart cart) {
        cart.getCartItems().clear();
        updateCart(cart);
    }


}
