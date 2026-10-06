package com.bookstore.dto;

import com.bookstore.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representation of a book in inventory")
public class BookResponseDTO {

    @Schema(description = "Unique Book identifier", example = "1")
    private Long id;

    @Schema(description = "Book title", example = "Clean Code")
    private String title;

    @Schema(description = "Book author", example = "Robert C. Martin")
    private String author;

    @Schema(description = "International Standard Book Number", example = "978-0132350884")
    private String isbn;

    @Schema(description = "Price in USD", example = "34.99")
    private BigDecimal price;

    @Schema(description = "Available inventory quantity", example = "15")
    private Integer stockQuantity;

    @Schema(description = "Genre or category", example = "Software Engineering")
    private String category;

    @Schema(description = "Creation timestamp")
    private Instant createdAt;

    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    public static BookResponseDTO fromEntity(Book book) {
        return BookResponseDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .category(book.getCategory())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
