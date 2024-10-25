package com.gridiron.ecommerce.order;

import com.gridiron.ecommerce.orderItem.OrderItemService;
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
    private final OrderItemService orderItemService;
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
     */
    @PutMapping("/admin/{orderId}")
    private ResponseEntity<ApiResponse> updatedOrderStatus(@RequestParam("status") OrderStatus status,
                                                           @PathVariable("orderId") Long orderId){
        orderService.updateOrderStatusByOrderId(orderId, status);
        return ResponseEntity.ok(new ApiResponse(true, "Order Status Updated Successfully", null));
    }

    /**
     * This endpoint fetches all the order in the system, with pagination support
     * It is secured and only accessible to authenticated users with ADMIN role
     * @param page The page to be fetched
     * @param size  The size of orders to be fetched in the specified page
     */
    @GetMapping("/admin")
    private ResponseEntity<ApiResponse> fetchAllOrders(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                       @RequestParam(value = "size", required = false, defaultValue = "10") int size){
        return ResponseEntity.ok(new ApiResponse(true, "Fetched All Orders Successfully", orderService.fetchAllOrders(page,size)));
    }


    /**
     * This endpoint fetches all the order items attached to an order with pagination support
     * It is secured and only accessible to authenticated users with ADMIN role
     * @param page The page to be fetched
     * @param size  The size of orders to be fetched in the specified page
     */
    @GetMapping("/admin/{order-id}")
    private ResponseEntity<ApiResponse> fetchAllOrderItem(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                                          @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                          @PathVariable("order-id") Long orderId){
        return ResponseEntity.ok(new ApiResponse(true, "Fetched All Orders Successfully", orderItemService.fetchAllOrderItemsByOrderId(orderId,page,size)));
    }


}
