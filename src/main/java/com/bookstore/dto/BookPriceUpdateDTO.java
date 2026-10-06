package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload for partially updating the price of an existing book")
public class BookPriceUpdateDTO {

    @NotNull(message = "price is required.")
    @Positive(message = "price must be greater than zero.")
    @Schema(description = "New updated price for the book", example = "29.99")
    private BigDecimal price;
}
