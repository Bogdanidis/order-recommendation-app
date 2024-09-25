package com.example.order_app.service;

import com.example.order_app.model.Order;
import com.example.order_app.model.OrderProduct;
import com.example.order_app.model.Product;
import com.example.order_app.repository.CustomerRepository;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.repository.UserRepository; // Assuming we also need to link to a user/customer
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository; // To find customer details if needed

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void orderProduct(Long productId, Integer quantity, Long customerId) {
        // Fetch product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if enough stock is available
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Update stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // Create a new Order
        Order order = new Order();

        // Link order to a customer if needed
        order.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found")));

        //Add date to order
        order.setDate(new java.sql.Date(System.currentTimeMillis()));

        //Add status to date
        order.setStatus("Pending");

        // Create an OrderProduct relationship
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setQuantity(quantity);
        orderProduct.setOrder(order);

        // Add OrderProduct to Order
        order.getOrderProducts().add(orderProduct);


        // Save the order
        orderRepository.save(order);
    }


}
