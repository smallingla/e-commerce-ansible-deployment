package com.gridiron.ecommerce.order;

import com.gridiron.ecommerce.utility.ApiResponse;
import com.gridiron.ecommerce.utility.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    /**
     * This endpoint create a new order for a user from the existing cart.
     * It is secured and only accessible to authenticated users with CUSTOMER role
     * @param token user authorization token, to extract userId from
     */
    @PostMapping("/customer")
    private ResponseEntity<ApiResponse> createOrderForUser(@RequestHeader("Authorization") String token){
        orderService.createOrderForUser(jwtService.extractUserId(token));
        return ResponseEntity.ok(new ApiResponse(true, "Order Placed Successfully", null));
    }

    /**
     * This endpoint updated the status of an order
     * It is secured and only accessible to authenticated users with ADMIN role
     * @param token user authorization token, to extract userId from
     */
    @PutMapping("/admin/{orderId}")
    private ResponseEntity<ApiResponse> updatedOrderStatus(@RequestHeader("Authorization") String token,
                                                           @RequestParam("status") OrderStatus status,
                                                           @PathVariable("orderId") Long orderId){
        orderService.updateOrderStatusByOrderId(orderId, status);
        return ResponseEntity.ok(new ApiResponse(true, "Order Status Updated Successfully", null));
    }


}
