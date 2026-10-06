package com.bookstore.controller;

import com.bookstore.dto.BookPriceUpdateDTO;
import com.bookstore.dto.BookRequestDTO;
import com.bookstore.dto.BookResponseDTO;
import com.bookstore.service.BookService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
@Tag(name = "Books Inventory", description = "RESTful endpoints for bookstore inventory management (Task 2)")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Fetch list of all books", description = "Retrieves all books in the inventory. Supports filtering by category via query parameter.")
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully")
    public ResponseEntity<List<BookResponseDTO>> getAllBooks(
            @Parameter(description = "Optional category filter", example = "Software Engineering")
            @RequestParam(value = "category", required = false) String category) {
        return ResponseEntity.ok(bookService.getAllBooks(category));
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Fetch details of a specific book by ID", description = "Retrieves full metadata for a single book identified by its ID path parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found and returned"),
            @ApiResponse(responseCode = "404", description = "Book not found with specified ID")
    })
    public ResponseEntity<BookResponseDTO> getBookById(
            @Parameter(description = "ID of the book to retrieve", example = "1")
            @PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }

    @PostMapping
    @Operation(summary = "Add a new book to the inventory", description = "Creates a new book record. Returns 201 Created along with the Location header pointing to the new resource.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or ISBN already exists")
    })
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookRequestDTO request) {
        BookResponseDTO createdBook = bookService.createBook(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBook.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdBook);
    }

    @PatchMapping("/{bookId}")
    @Operation(summary = "Update price of an existing book", description = "Partially updates only the price field of the targeted book resource.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book price successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid price value"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<BookResponseDTO> updateBookPrice(
            @Parameter(description = "ID of the book to update", example = "1")
            @PathVariable("bookId") Long bookId,
            @Valid @RequestBody BookPriceUpdateDTO request) {
        return ResponseEntity.ok(bookService.updateBookPrice(bookId, request));
    }

    @DeleteMapping("/{bookId}")
    @Operation(summary = "Delete a book record from inventory", description = "Removes the book record permanently. Returns 204 No Content upon success.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID of the book to delete", example = "1")
            @PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}
