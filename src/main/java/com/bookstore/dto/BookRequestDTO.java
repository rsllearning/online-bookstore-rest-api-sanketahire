package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload for adding a new book to the inventory")
public class BookRequestDTO {

    @NotBlank(message = "title is required and cannot be empty.")
    @Schema(description = "Book title", example = "Clean Architecture")
    private String title;

    @NotBlank(message = "author is required and cannot be empty.")
    @Schema(description = "Book author", example = "Robert C. Martin")
    private String author;

    @NotBlank(message = "isbn is required and cannot be empty.")
    @Schema(description = "International Standard Book Number", example = "978-0134494166")
    private String isbn;

    @NotNull(message = "price is required.")
    @Positive(message = "price must be a positive number.")
    @Schema(description = "Book price", example = "39.99")
    private BigDecimal price;

    @NotNull(message = "stockQuantity is required.")
    @Min(value = 0, message = "stockQuantity cannot be negative.")
    @Schema(description = "Initial inventory stock count", example = "25")
    private Integer stockQuantity;

    @NotBlank(message = "category is required.")
    @Schema(description = "Genre or category", example = "Software Engineering")
    private String category;
}
