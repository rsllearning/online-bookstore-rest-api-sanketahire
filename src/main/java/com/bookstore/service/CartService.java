package com.bookstore.service;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.dto.CartResponseDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.exception.InsufficientStockException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    /**
     * Task 1: Add to Cart Request Lifecycle Implementation
     * 1. Auth/User Check: Resolves the authenticated user ID.
     * 2. Data Validation: Ensures bookId exists and quantity is within permitted limits.
     * 3. Inventory Availability: Checks real-time stock in database.
     * 4. Business Rules: Enforces max cart limits per user (e.g. max 10 per title).
     * 5. State Persistence: Saves/updates the CartItem entity in H2 database.
     */
    @Transactional
    public CartResponseDTO addItemToCart(String userId, CartItemRequestDTO request) {
        // Step 1: Validate book exists in database
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));

        // Step 2: Check current cart quantity for this book
        CartItem existingItem = cartItemRepository.findByUserIdAndBookId(userId, request.getBookId())
                .orElse(null);

        int currentCartQty = (existingItem != null) ? existingItem.getQuantity() : 0;
        int newTotalQty = currentCartQty + request.getQuantity();

        // Step 3: Enforce business logic rule: Max 10 units per title per cart
        if (newTotalQty > 10) {
            throw new IllegalArgumentException(String.format(
                    "Cart limit exceeded. Maximum 10 units allowed per title. Current in cart: %d, trying to add: %d",
                    currentCartQty, request.getQuantity()));
        }

        // Step 4: Enforce inventory availability check
        if (book.getStockQuantity() < newTotalQty) {
            throw new InsufficientStockException(book.getId(), newTotalQty, book.getStockQuantity());
        }

        // Step 5: Persist to database
        if (existingItem != null) {
            existingItem.setQuantity(newTotalQty);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .userId(userId)
                    .bookId(book.getId())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        // Step 6: Return the complete updated cart snapshot
        return getCartSnapshot(userId);
    }

    @Transactional(readOnly = true)
    public CartResponseDTO getCartSnapshot(String userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        List<CartResponseDTO.CartItemDetailDTO> itemDetails = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cartItems) {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null) {
                BigDecimal itemTotal = book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                subtotal = subtotal.add(itemTotal);
                totalItems += item.getQuantity();

                itemDetails.add(CartResponseDTO.CartItemDetailDTO.builder()
                        .bookId(book.getId())
                        .bookTitle(book.getTitle())
                        .unitPrice(book.getPrice())
                        .quantity(item.getQuantity())
                        .itemTotal(itemTotal)
                        .build());
            }
        }

        return CartResponseDTO.builder()
                .userId(userId)
                .items(itemDetails)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .build();
    }
}
