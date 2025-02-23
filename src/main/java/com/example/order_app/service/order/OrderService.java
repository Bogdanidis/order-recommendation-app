package com.example.order_app.service.order;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.enums.OrderStatus;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.Order;
import com.example.order_app.model.OrderItem;
import com.example.order_app.model.Product;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.service.cart.CartService;
import com.example.order_app.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;


    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(this::convertToDto);
    }

    //    @Transactional
//    @Override
//    public Order placeOrder(Long userId) {
//        Cart cart   = cartService.getCartByUserId(userId);
//        if (cart == null || cart.getItems().isEmpty()) {
//            throw new ResourceNotFoundException("Cart is empty or not found");
//        }
//        Order order = createOrder(cart);
//        List<OrderItem> orderItemList = createOrderItems(order, cart);
//        order.setOrderItems(new HashSet<>(orderItemList));
//        order.setTotalAmount(calculateTotalAmount(orderItemList));
//        //Save the order
//        Order savedOrder = orderRepository.save(order);
//        //clear the cart
//        cartService.clearCart(cart.getId());
//        return savedOrder;
//    }
    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty or not found");
        }
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart.getId());
        return savedOrder;
    }
    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return  order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return  cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            return  new OrderItem(
                    order,
                    product,
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice());
        }).toList();

    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return  orderItemList
                .stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this :: convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return  orders.stream().map(this :: convertToDto).toList();
    }

    @Override
    public Page<OrderDto> getUserOrdersPaginated(Long userId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        return orderPage.map(this::convertToDto);
    }


    @Transactional
    @Override
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or does not belong to the user"));

        if ((order.getOrderStatus() != OrderStatus.PENDING) && (order.getOrderStatus() != OrderStatus.PROCESSING) ) {
            throw new IllegalStateException("Only pending or processing orders can be cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);


        // Return items to inventory
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());
            productRepository.save(product);
        });

        // refund payment would be here.
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if ((order.getOrderStatus() != OrderStatus.PENDING) && (order.getOrderStatus() != OrderStatus.PROCESSING) ) {
            throw new IllegalStateException("Only pending or processing orders can be cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);


        // Return items to inventory
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());
            productRepository.save(product);
        });

        // refund payment would be here.
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.findById(orderId)
                .ifPresentOrElse(
                        product -> orderRepository.softDelete(orderId, LocalDateTime.now()),
                        () -> { throw new ResourceNotFoundException("Order not found!"); }
                );
    }

    @Override
    public Long countTodaysOrders() {
        LocalDate today = LocalDate.now();
        return orderRepository.countByOrderDateAndOrderStatusNot(today, OrderStatus.CANCELLED);
    }

    @Override
    public BigDecimal getTodaysRevenue() {
        LocalDate today = LocalDate.now();
        List<Order> todayOrders = orderRepository.findByOrderDateAndOrderStatusNot(today, OrderStatus.CANCELLED);
        return todayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
