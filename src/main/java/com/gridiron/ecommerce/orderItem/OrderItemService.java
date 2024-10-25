package com.gridiron.ecommerce.orderItem;

import com.gridiron.ecommerce.cartItem.CartItem;
import com.gridiron.ecommerce.cartItem.response.CartItemResponse;
import com.gridiron.ecommerce.orderItem.response.OrderItemResponse;
import com.gridiron.ecommerce.product.ProductService;
import com.gridiron.ecommerce.utility.PaginatedData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;


    public OrderItemService(OrderItemRepository orderItemRepository, ProductService productService) {
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
    }


    public PaginatedData fetchAllOrderItemsByOrderId(Long orderId, int page, int size){

        Page<OrderItem> orderItems = orderItemRepository.findByOrderId(
                orderId, PageRequest.of(page-1, size, Sort.by(Sort.Order.desc("createdAt"))));

        return PaginatedData.builder()
                .currentSize(orderItems.getNumberOfElements())
                .totalSize(orderItems.getTotalElements())
                .totalPage(orderItems.getTotalPages())
                .data(formatOrderItems(orderItems.getContent()))
                .build();
    }


    List<OrderItemResponse> formatOrderItems(List<OrderItem> orderItems) {
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {

            orderItemResponses.add(new OrderItemResponse(
                    orderItem.getId(),
                    productService.formatProductsToProductResponse(Set.of(orderItem.getProduct())).get(0),
                    orderItem.getQuantity()
            ));
        }
        return orderItemResponses;
    }
}
