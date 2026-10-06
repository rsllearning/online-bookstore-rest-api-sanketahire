package com.bookstore.dto;

import com.bookstore.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Line item detail in an order response")
public class OrderItemResponseDTO {

    @Schema(description = "Book identifier", example = "1")
    private Long bookId;

    @Schema(description = "Title of the book", example = "Clean Code")
    private String bookTitle;

    @Schema(description = "Price per unit in USD", example = "34.99")
    private BigDecimal unitPrice;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Subtotal for this line item", example = "69.98")
    private BigDecimal subtotal;

    public static OrderItemResponseDTO fromEntity(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .bookId(item.getBookId())
                .bookTitle(item.getBookTitle())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }
}
