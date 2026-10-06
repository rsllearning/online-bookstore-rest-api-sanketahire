package com.bookstore.controller;

import com.bookstore.dto.CartItemRequestDTO;
import com.bookstore.dto.CartResponseDTO;
import com.bookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Endpoints demonstrating Task 1 Add-to-Cart request lifecycle")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    @Operation(summary = "Add an item to the user's shopping cart",
            description = "Task 1 Request Lifecycle endpoint. Validates auth token, book inventory, applies business rules, and updates cart in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item successfully added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or cart limits exceeded"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "422", description = "Requested quantity exceeds available stock")
    })
    public ResponseEntity<CartResponseDTO> addItemToCart(
            @Parameter(description = "Bearer JWT token or user ID (Defaults to simulated user usr_1001 if omitted)", example = "Bearer eyJhbGciOi...")
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CartItemRequestDTO request) {

        // Extract user identity from Authorization header
        String userId = "usr_1001";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty() && !token.startsWith("eyJ")) {
                userId = token;
            }
        }

        CartResponseDTO updatedCart = cartService.addItemToCart(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCart);
    }

    @GetMapping
    @Operation(summary = "Get current user's shopping cart", description = "Retrieves current items and calculated subtotal for user.")
    public ResponseEntity<CartResponseDTO> getCart(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String userId = "usr_1001";
        return ResponseEntity.ok(cartService.getCartSnapshot(userId));
    }
}
