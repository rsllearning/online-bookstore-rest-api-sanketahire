package com.bookstore.dto;

import com.bookstore.entity.Order;
import com.bookstore.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order response object returned upon creation and retrieval")
public class OrderResponseDTO {

    @Schema(description = "Unique order identifier", example = "ord_1728224400_9182")
    private String orderId;

    @Schema(description = "User ID who owns this order", example = "usr_1001")
    private String userId;

    @Schema(description = "Current lifecycle status of the order", example = "CONFIRMED")
    private OrderStatus status;

    @Schema(description = "Total purchase amount in USD", example = "69.98")
    private BigDecimal totalAmount;

    @Schema(description = "Destination shipping address formatted string")
    private String shippingAddress;

    @Schema(description = "Payment identifier or status", example = "pm_card_visa_4242")
    private String paymentMethodId;

    @Schema(description = "List of ordered items with prices and quantities")
    private List<OrderItemResponseDTO> items;

    @Schema(description = "Order placement UTC timestamp")
    private Instant createdAt;

    public static OrderResponseDTO fromEntity(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(OrderItemResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentMethodId(order.getPaymentMethodId())
                .items(itemDTOs)
                .createdAt(order.getOrderDate())
                .build();
    }
}
