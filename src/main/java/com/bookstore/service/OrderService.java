package com.bookstore.service;

import com.bookstore.dto.OrderItemRequestDTO;
import com.bookstore.dto.OrderRequestDTO;
import com.bookstore.dto.OrderResponseDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Order;
import com.bookstore.entity.OrderItem;
import com.bookstore.entity.OrderStatus;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    /**
     * Places a new book order with atomic stock validation and inventory deduction.
     */
    @Transactional
    public OrderResponseDTO createOrder(String resolvedUserId, OrderRequestDTO request) {
        String userId = (request.getUserId() != null && !request.getUserId().trim().isEmpty())
                ? request.getUserId()
                : resolvedUserId;

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Step 1: Validate stock and calculate totals for all items atomically
        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + itemRequest.getBookId()));

            // Inventory validation check
            if (book.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(book.getId(), itemRequest.getQuantity(), book.getStockQuantity());
            }

            // Deduct inventory atomically
            book.setStockQuantity(book.getStockQuantity() - itemRequest.getQuantity());
            bookRepository.save(book);

            BigDecimal lineSubtotal = book.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(lineSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .bookId(book.getId())
                    .bookTitle(book.getTitle())
                    .unitPrice(book.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(lineSubtotal)
                    .build();

            orderItems.add(orderItem);
        }

        // Step 2: Format shipping address
        String formattedAddress = String.format("%s, %s, %s %s, %s",
                request.getShippingAddress().getStreet(),
                request.getShippingAddress().getCity(),
                request.getShippingAddress().getState(),
                request.getShippingAddress().getPostalCode(),
                request.getShippingAddress().getCountry());

        // Step 3: Create and persist the Order entity
        String orderId = "ord_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 4);

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .orderDate(Instant.now())
                .status(OrderStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .shippingAddress(formattedAddress)
                .paymentMethodId(request.getPaymentMethodId() != null ? request.getPaymentMethodId() : "pm_card_default")
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orders.stream().map(OrderResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return OrderResponseDTO.fromEntity(order);
    }
}
