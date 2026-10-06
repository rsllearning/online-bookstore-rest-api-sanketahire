package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Individual item in an order placement request")
public class OrderItemRequestDTO {

    @NotNull(message = "bookId is required.")
    @Schema(description = "ID of the book to purchase", example = "1")
    private Long bookId;

    @NotNull(message = "quantity is required.")
    @Min(value = 1, message = "quantity must be at least 1.")
    @Schema(description = "Number of copies to purchase", example = "2")
    private Integer quantity;
}
