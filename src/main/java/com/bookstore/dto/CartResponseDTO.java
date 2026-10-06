package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Shopping cart state response")
public class CartResponseDTO {

    @Schema(description = "User identifier", example = "usr_9981")
    private String userId;

    @Schema(description = "List of items in the cart")
    private List<CartItemDetailDTO> items;

    @Schema(description = "Total number of items in cart", example = "3")
    private Integer totalItems;

    @Schema(description = "Calculated cart subtotal amount", example = "69.98")
    private BigDecimal subtotal;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemDetailDTO {
        private Long bookId;
        private String bookTitle;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal itemTotal;
    }
}
