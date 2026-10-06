package com.bookstore.exception;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final Long bookId;
    private final int requestedQuantity;
    private final int availableStock;

    public InsufficientStockException(Long bookId, int requestedQuantity, int availableStock) {
        super(String.format("Requested quantity (%d) exceeds available stock (%d) for book ID %d.",
                requestedQuantity, availableStock, bookId));
        this.bookId = bookId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }
}
