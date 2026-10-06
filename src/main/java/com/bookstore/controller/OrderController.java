package com.bookstore.controller;

import com.bookstore.dto.OrderRequestDTO;
import com.bookstore.dto.OrderResponseDTO;
import com.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders & Purchases", description = "RESTful endpoints for placing and querying book orders (Tasks 2 & 3)")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/v1/orders")
    @Operation(summary = "Place a new book order",
            description = "Creates a new purchase order. Validates stock availability, atomically decrements inventory, and returns 201 Created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or missing fields"),
            @ApiResponse(responseCode = "404", description = "Book ID not found in inventory"),
            @ApiResponse(responseCode = "422", description = "Insufficient inventory stock for requested items")
    })
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Parameter(description = "Bearer JWT token (Optional in demo)", example = "Bearer eyJhbGciOi...")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody OrderRequestDTO request) {

        String resolvedUserId = "usr_1001";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            resolvedUserId = "usr_" + Math.abs(authHeader.substring(7).hashCode() % 10000);
        }

        OrderResponseDTO createdOrder = orderService.createOrder(resolvedUserId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getOrderId())
                .toUri();

        return ResponseEntity.created(location).body(createdOrder);
    }

    @GetMapping("/v1/users/{userId}/orders")
    @Operation(summary = "Retrieve all orders belonging to a specific user",
            description = "Fetches complete order history for the specified user ID.")
    @ApiResponse(responseCode = "200", description = "User orders retrieved successfully")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(
            @Parameter(description = "Target user identifier", example = "usr_1001")
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/v1/orders/{orderId}")
    @Operation(summary = "Get order details by order ID", description = "Retrieves complete metadata and line items for a specific order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "Order ID", example = "ord_1728224400_9182")
            @PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}
