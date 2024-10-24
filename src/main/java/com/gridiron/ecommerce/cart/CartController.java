package com.gridiron.ecommerce.cart;

import com.gridiron.ecommerce.cartItem.CartItemService;
import com.gridiron.ecommerce.cartItem.request.CreateCartItemRequest;
import com.gridiron.ecommerce.utility.ApiResponse;
import com.gridiron.ecommerce.utility.security.JwtService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/api/v1/carts")
@RestController
public class CartController {

    private final CartService cartService;
    private final JwtService jwtService;
    private final CartItemService cartItemService;


    /**
     * This endpoint add a new cartItem to the users cart.
     * It is secured and only accessible to authorized users with role CUSTOMER
     * @param createCartItemRequest json request of cart item, including productId, and qunantity
     * @param token user authorization token, to extract userId from
     */
    @PostMapping("/private")
    private ResponseEntity<ApiResponse> addItemToCart(@RequestBody @Valid CreateCartItemRequest createCartItemRequest,
                                                      @RequestHeader("Authorization") String token){

        System.out.println(createCartItemRequest);
        cartService.addItemToCart(jwtService.extractUserId(token),createCartItemRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Added to cart successfully", null));
    }

    /**
     * This endpoint removes a new cartItem to the users cart.
     * It is secured and only accessible to authorized users with role CUSTOMER
     * @param cartItemId The id of the cartItem to be removed
     * @param token user authorization token, to extract userId from
     */
    @PutMapping("/private")
    private ResponseEntity<ApiResponse> removeItemFromCart(@RequestHeader("Authorization") String token,
                                                           @RequestParam("cartItemId") Long cartItemId){

        cartService.deleteItemFromCart(jwtService.extractUserId(token),cartItemId);

        return ResponseEntity.ok(new ApiResponse(true, "Product removed successfully", null));
    }

    /**
     * This endpoint fetches the items in a user cart
     * It is secured and only accessible to authorized users with role CUSTOMER
     * @param token user authorization token, to extract userId from
     * @param page the page number to be fetched
     * @param size the size of items to be fetched for the page
     */
    @GetMapping("/private")
    private ResponseEntity<ApiResponse> fetchItemInCart(@RequestHeader("Authorization") String token,
                                                        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") int size){

        return ResponseEntity.ok(new ApiResponse(true, "Product fetched successfully",
                cartService.fetchProductInCartByCartId(jwtService.extractUserId(token), page, size)));
    }



}
