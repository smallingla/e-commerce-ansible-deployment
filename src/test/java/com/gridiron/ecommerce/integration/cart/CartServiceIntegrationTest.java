package com.gridiron.ecommerce.integration.cart;

import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cart.CartService;
import com.gridiron.ecommerce.cartItem.CartItemRepository;
import com.gridiron.ecommerce.cartItem.request.CreateCartItemRequest;
import com.gridiron.ecommerce.integration.AbstractIntegrationTest;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class CartServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long userId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // Create a product for testing
        Product product = new Product("Test Product", new BigDecimal("100.0"), "A test product", 10);
        productRepository.save(product);
        productId = product.getId();

        // Define a test user ID
        userId = 1L;
    }


    @Test
    void addItemToCart_ShouldAddItem_WhenProductExists() {
        // Arrange
        CreateCartItemRequest request = new CreateCartItemRequest(productId, 2);

        // Act
        cartService.addItemToCart(userId, request);

        // Assert
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        assertTrue(cartOptional.isPresent());

        Cart cart = cartOptional.get();
        assertEquals(1, cart.getCartItems().size());
        assertEquals(productId, cart.getCartItems().iterator().next().getProduct().getId());
        assertEquals(2, cart.getCartItems().iterator().next().getQuantity());
    }

    @Test
    void deleteItemFromCart_ShouldRemoveItem_WhenItemExists() {
        // Arrange
        CreateCartItemRequest request = new CreateCartItemRequest(productId, 2);
        cartService.addItemToCart(userId, request);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        Long cartItemId = cart.getCartItems().iterator().next().getId();

        // Act
        cartService.deleteItemFromCart(userId, cartItemId);

        // Assert
        cart = cartRepository.findByUserId(userId).orElseThrow();
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void deleteItemFromCart_ShouldThrowException_WhenItemDoesNotExist() {
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                cartService.deleteItemFromCart(userId, 999L));
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    @Transactional
    void fetchProductInCartByCartId_ShouldReturnPaginatedData_WhenItemsExist() {
        // Arrange
        CreateCartItemRequest request = new CreateCartItemRequest(productId, 2);
        cartService.addItemToCart(userId, request);

        // Act
        PaginatedData paginatedData = cartService.fetchProductInCartByCartId(userId, 1, 10);

        // Assert
        assertNotNull(paginatedData);
        assertEquals(1, paginatedData.currentSize());
        assertEquals(1, paginatedData.totalSize());
        assertEquals(1, paginatedData.totalPage());
    }

    @Test
    void clearCart_ShouldEmptyCartItems_WhenCartIsNotEmpty() {
        // Arrange
        CreateCartItemRequest request = new CreateCartItemRequest(productId, 2);
        cartService.addItemToCart(userId, request);
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();

        // Act
        cartService.clearCart(cart);

        // Assert
        cart = cartRepository.findByUserId(userId).orElseThrow();
        assertTrue(cart.getCartItems().isEmpty());
    }
}
