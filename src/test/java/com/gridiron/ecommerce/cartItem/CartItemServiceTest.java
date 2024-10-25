package com.gridiron.ecommerce.cartItem;

import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.cartItem.CartItemRepository;
import com.gridiron.ecommerce.cartItem.CartItemService;
import com.gridiron.ecommerce.cartItem.response.CartItemResponse;
import com.gridiron.ecommerce.product.Product;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.product.response.ProductResponse;
import com.gridiron.ecommerce.utility.PaginatedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void fetchCartItemsByCartId_ShouldReturnPaginatedData_WhenCartItemsExist() {
        // Arrange
        Long cartId = 1L;
        int page = 1;
        int size = 10;

        // Mock data setup
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100.00));

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);

        Page<CartItem> cartItemPage = new PageImpl<>(List.of(cartItem), PageRequest.of(page - 1, size), 1);
        when(cartItemRepository.findAllByCartId(cartId, PageRequest.of(page - 1, size)))
                .thenReturn(cartItemPage);

        ProductResponse productResponse = new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAvailabilityQuantity(),
                product.getPrice()
        );
        when(productService.formatProductsToProductResponse(Set.of(product)))
                .thenReturn(List.of(productResponse));

        // Act
        PaginatedData result = cartItemService.fetchCartItemsByCartId(cartId, page, size);

        // Assert
        assertEquals(1, result.totalPage());
        assertEquals(1, result.currentSize());
        assertEquals(1, result.totalSize());

        // Ensure the retrieved data is of the expected type
        List<CartItemResponse> data = (List<CartItemResponse>) result.data();
        CartItemResponse response = data.get(0);
        assertEquals(productResponse.productId(), response.product().productId());
    }
}
