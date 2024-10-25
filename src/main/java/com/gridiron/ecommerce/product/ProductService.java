package com.gridiron.ecommerce.product;

import com.gridiron.ecommerce.cartItem.CartItemRepository;
import com.gridiron.ecommerce.orderItem.OrderItem;
import com.gridiron.ecommerce.product.request.CreateProductRequest;
import com.gridiron.ecommerce.product.request.EditProductRequest;
import com.gridiron.ecommerce.product.response.ProductResponse;
import com.gridiron.ecommerce.utility.PaginatedData;
import com.gridiron.ecommerce.utility.exception.InvalidInputException;
import com.gridiron.ecommerce.utility.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;


    /**
     *
     * @param existingProduct
     */
    private void updateProduct(Product existingProduct) {
        existingProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(existingProduct);
    }

    /**
     * This method creates a new product
     * @param createProductRequest json request for creating a product. it contains name, price, description, and quantity
     * @return returns the product response of the created product.
     */
    public ProductResponse createProduct(CreateProductRequest createProductRequest) {
        Product product = new Product(
                createProductRequest.name(),
                createProductRequest.price(),
                createProductRequest.description(),
                createProductRequest.availabilityQuantity()
        );
        productRepository.save(product);

        return convertToProductResponse(product);
    }

    private static ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .availabilityQuantity(product.getAvailabilityQuantity())
                .price(product.getPrice())
                .build();
    }

    /**
     * This method fetches all the products, with pagination implementation, sorted by the date created in ascending order
     * @param page page to be fetched
     * @param size quantity of products to be fetched
     * @return a PaginatedData object that includes the totalPages, currentSize, totalSize, and the productResponse
     */
    public PaginatedData fetchProducts(int page, int size){

        if(page<=0){
            throw new InvalidInputException("Page cannot be less than or equal to zero");
        }

        Page<Product> products = productRepository.findAll(
                PageRequest.of(page-1, size, Sort.by(Sort.Order.desc("createdAt"))));

        return PaginatedData.builder()
                .totalPage(products.getTotalPages())
                .currentSize(products.getNumberOfElements())
                .totalSize(products.getTotalElements())
                .data(formatProductsToProductResponse(products.getContent()))
                .build();

    }

    /**
     * This method converts a collection of products to another list of product response
     * @param products collection of products
     * @return list of ProductResponse object
     */
    public List<ProductResponse> formatProductsToProductResponse(Collection<Product> products) {

        List<ProductResponse> productResponses = new ArrayList<>();

        products.forEach(product -> {
            productResponses.add(
                    ProductResponse.builder()
                            .productId(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .availabilityQuantity(product.getAvailabilityQuantity())
                            .price(product.getPrice())
                            .build()
            );
        });

        return productResponses;
    }


    /**
     * This method edits a products
     * @param productId this is the ID of the product to be edited
     * @param editProductRequest this is the request object to edit the product, it contains: name, price, description, quantity
     */
    public ProductResponse editProduct(Long productId, EditProductRequest editProductRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setName(editProductRequest.name());
        product.setDescription(editProductRequest.description());
        product.setAvailabilityQuantity(editProductRequest.availabilityQuantity());
        product.setPrice(editProductRequest.price());

        updateProduct(product);

        return convertToProductResponse(product);
    }

    /**
     * This method deletes a products
     * @param productId this is the ID of the product to be deleted
     */
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
        //delete all corresponding cart_item that is associated to the product
        cartItemRepository.deleteByProductId(productId);
    }


    /**
     * Updates the availability quantity of products based on the provided order items.
     * This method adjusts the availability quantity of products by deducting or adding
     * the quantity specified in each OrderItem.
     *
     * @param orderItems A collection of OrderItem objects containing the products and their quantities.
     * @param deduct A boolean flag indicating whether to deduct (true) or add (false) the quantities.
     */
    public void updateProductQuantityFromOrderItem(Collection<OrderItem> orderItems, boolean deduct) {

        List<Product> products = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();

            if(deduct){
                if(product.getAvailabilityQuantity()!=0){
                    product.setAvailabilityQuantity(product.getAvailabilityQuantity()-orderItem.getQuantity());
                    product.setUpdatedAt(LocalDateTime.now());
                    products.add(product);
                }
            }else{
                product.setAvailabilityQuantity(product.getAvailabilityQuantity() + orderItem.getQuantity());
                product.setUpdatedAt(LocalDateTime.now());
                products.add(product);
            }

            productRepository.saveAll(products);
        }
    }






}
