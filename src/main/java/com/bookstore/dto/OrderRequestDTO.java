package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload for placing a new book order (POST /v1/orders)")
public class OrderRequestDTO {

    @Schema(description = "User placing the order. If omitted, resolved from Authorization token.", example = "usr_1001")
    private String userId;

    @NotEmpty(message = "Order must contain at least one item.")
    @Valid
    @Schema(description = "List of items in the order")
    private List<OrderItemRequestDTO> items;

    @NotNull(message = "shippingAddress is required.")
    @Valid
    @Schema(description = "Shipping destination address")
    private AddressDTO shippingAddress;

    @Schema(description = "Payment token or method ID", example = "pm_card_visa_4242")
    private String paymentMethodId;
}
