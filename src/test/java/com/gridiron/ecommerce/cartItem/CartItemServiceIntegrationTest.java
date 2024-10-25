package com.gridiron.ecommerce.cartItem;

import com.gridiron.ecommerce.AbstractIntegrationTest;
import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cartItem.response.CartItemResponse;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.utility.PaginatedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class CartItemServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    private Long cartId;
    private Long productId;

    @BeforeEach
    void setUp() {
        // Create a product for testing
        Product product = new Product("Test Product", new BigDecimal("100.0"), "A test product", 10);
        productRepository.save(product);
        productId = product.getId();

        // Create a cart for testing
        Cart cart = new Cart();
        cart.setUserId(1L);
        cartRepository.save(cart);
        cartId = cart.getId();

        // Add a CartItem to the cart
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItemRepository.save(cartItem);
    }

    @Test
    void fetchCartItemsByCartId_ShouldReturnPaginatedData_WhenItemsExist() {
        // Act
        PaginatedData paginatedData = cartItemService.fetchCartItemsByCartId(cartId, 1, 10);

        // Assert
        assertThat(paginatedData).isNotNull();
        assertThat(paginatedData.totalSize()).isGreaterThan(0);
        assertThat(paginatedData.currentSize()).isEqualTo(1);
        assertThat(paginatedData.totalPage()).isEqualTo(1);

        // Further assertions on cart item response
        List<CartItemResponse> cartItemResponses = (List<CartItemResponse>) paginatedData.data();
        assertThat(cartItemResponses).isNotEmpty();
        assertThat(cartItemResponses.get(0).quantity()).isEqualTo(2);
        assertThat(cartItemResponses.get(0).product().name()).isEqualTo("Test Product");
    }
}