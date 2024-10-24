package com.gridiron.ecommerce.unit.cart;

import com.gridiron.ecommerce.cart.Cart;
import com.gridiron.ecommerce.cart.CartRepository;
import com.gridiron.ecommerce.cart.CartService;
import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.cartItem.CartItemService;
import com.gridiron.ecommerce.cartItem.request.CreateCartItemRequest;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductRepository;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void addItemToCart_ShouldAddNewItem_WhenCartDoesNotExist() {
        // Arrange
        Long userId = 1L;
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 2);
        Product product = new Product("Test Product", BigDecimal.valueOf(100), "A sample product", 10);
        when(productRepository.findById(request.productId())).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        cartService.addItemToCart(userId, request);

        // Assert
        verify(cartRepository, times(2)).save(any(Cart.class));
    }

    @Test
    void addItemToCart_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        Long userId = 1L;
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 2);
        when(productRepository.findById(request.productId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.addItemToCart(userId, request));
    }

    @Test
    void deleteItemFromCart_ShouldRemoveItem_WhenItemExists() {
        // Arrange
        Long userId = 1L;
        Long cartItemId = 1L;
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cart.getCartItems().add(cartItem);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act
        cartService.deleteItemFromCart(userId, cartItemId);

        // Assert
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void deleteItemFromCart_ShouldThrowException_WhenItemDoesNotExist() {
        // Arrange
        Long userId = 1L;
        Long cartItemId = 99L;
        Cart cart = new Cart();
        cart.setUserId(userId);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.deleteItemFromCart(userId, cartItemId);
        });
    }

    @Test
    void fetchProductInCartByCartId_ShouldReturnPaginatedData_WhenCartExists() {
        // Arrange
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);
        cartItems.add(cartItem);

        PaginatedData paginatedData = PaginatedData.builder()
                .currentSize(cartItems.size())
                .totalPage(1)
                .totalSize((long) cartItems.size())
                .data(cartItems) // Populate with cart items
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemService.fetchCartItemsByCartId(cart.getId(), 1, 10)).thenReturn(paginatedData);

        // Act
        PaginatedData result = cartService.fetchProductInCartByCartId(userId, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.totalPage());
        assertEquals(1, result.currentSize());
        assertEquals(1, result.totalSize());
        verify(cartItemService, times(1)).fetchCartItemsByCartId(cart.getId(), 1, 10);
    }

    @Test
    void clearCart_ShouldEmptyCartItems_WhenCartIsNotEmpty() {

        // Arrange
        Long userId = 1L;
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(new CartItem());
        cart.setCartItems(cartItems);

        // Act
        cartService.clearCart(cart);

        // Assert
        verify(cartRepository, times(1)).save(cart);
    }
}
