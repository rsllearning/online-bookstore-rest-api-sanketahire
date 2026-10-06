package com.bookstore;

import com.bookstore.dto.*;
import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Online Bookstore REST API Integration Tests")
class BookstoreApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Long cleanCodeBookId;
    private Long dddaBookId;
    private Long springBootBookId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        bookRepository.deleteAll();

        // Seed clean, predictable test fixtures
        Book book1 = bookRepository.save(Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .price(new BigDecimal("34.99"))
                .stockQuantity(15)
                .category("Software Engineering")
                .build());
        cleanCodeBookId = book1.getId();

        Book book2 = bookRepository.save(Book.builder()
                .title("Designing Data-Intensive Applications")
                .author("Martin Kleppmann")
                .isbn("978-1449373320")
                .price(new BigDecimal("45.00"))
                .stockQuantity(20)
                .category("Distributed Systems")
                .build());
        dddaBookId = book2.getId();

        Book book3 = bookRepository.save(Book.builder()
                .title("The Pragmatic Programmer")
                .author("Andrew Hunt & David Thomas")
                .isbn("978-0135957059")
                .price(new BigDecimal("39.99"))
                .stockQuantity(8)
                .category("Software Engineering")
                .build());

        Book book4 = bookRepository.save(Book.builder()
                .title("Spring Boot in Action")
                .author("Craig Walls")
                .isbn("978-1617292545")
                .price(new BigDecimal("29.99"))
                .stockQuantity(2) // Intentionally low stock
                .category("Frameworks")
                .build());
        springBootBookId = book4.getId();
    }

    // ==========================================
    // Task 1: Add to Cart Lifecycle Tests
    // ==========================================

    @Test
    @DisplayName("Task 1: POST /v1/cart/items - Happy Path: Successfully add book to cart")
    void addItemToCart_validRequest_returns200() throws Exception {
        CartItemRequestDTO request = CartItemRequestDTO.builder()
                .bookId(cleanCodeBookId)
                .quantity(2)
                .build();

        mockMvc.perform(post("/v1/cart/items")
                        .header("Authorization", "Bearer usr_1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("usr_1001")))
                .andExpect(jsonPath("$.totalItems", is(2)))
                .andExpect(jsonPath("$.subtotal", is(69.98)));
    }

    @Test
    @DisplayName("Task 1: POST /v1/cart/items - Unhappy Path: Non-existent book ID returns 404")
    void addItemToCart_nonExistentBook_returns404() throws Exception {
        CartItemRequestDTO request = CartItemRequestDTO.builder()
                .bookId(999999L)
                .quantity(1)
                .build();

        mockMvc.perform(post("/v1/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")));
    }

    // ==========================================
    // Task 2: Books Inventory REST Endpoints Tests
    // ==========================================

    @Test
    @DisplayName("Task 2: GET /v1/books - Returns list of all books with 200 OK")
    void getAllBooks_returns200() throws Exception {
        mockMvc.perform(get("/v1/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].price", notNullValue()));
    }

    @Test
    @DisplayName("Task 2: GET /v1/books/{id} - Returns details of a specific book by ID with 200 OK")
    void getBookById_whenExists_returns200() throws Exception {
        mockMvc.perform(get("/v1/books/" + cleanCodeBookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cleanCodeBookId.intValue())))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")))
                .andExpect(jsonPath("$.price", is(34.99)));
    }

    @Test
    @DisplayName("Task 2: GET /v1/books/{id} - Unhappy Path: Non-existent book returns 404 Not Found")
    void getBookById_whenNotExists_returns404() throws Exception {
        mockMvc.perform(get("/v1/books/999999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.code", is("RESOURCE_NOT_FOUND")));
    }

    @Test
    @DisplayName("Task 2: POST /v1/books - Creates new book, returns 201 Created and Location header")
    void createBook_withValidPayload_returns201() throws Exception {
        BookRequestDTO request = BookRequestDTO.builder()
                .title("Building Microservices")
                .author("Sam Newman")
                .isbn("978-1492034025")
                .price(new BigDecimal("42.50"))
                .stockQuantity(30)
                .category("Cloud Architecture")
                .build();

        mockMvc.perform(post("/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Building Microservices")))
                .andExpect(jsonPath("$.price", is(42.50)));
    }

    @Test
    @DisplayName("Task 2: PATCH /v1/books/{id} - Updates price of existing book, returns 200 OK")
    void updateBookPrice_withValidPrice_returns200() throws Exception {
        BookPriceUpdateDTO request = BookPriceUpdateDTO.builder()
                .price(new BigDecimal("37.99"))
                .build();

        mockMvc.perform(patch("/v1/books/" + cleanCodeBookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cleanCodeBookId.intValue())))
                .andExpect(jsonPath("$.price", is(37.99)));
    }

    @Test
    @DisplayName("Task 2: DELETE /v1/books/{id} - Deletes book record, returns 204 No Content")
    void deleteBook_whenExists_returns204() throws Exception {
        mockMvc.perform(delete("/v1/books/" + cleanCodeBookId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/books/" + cleanCodeBookId))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // Task 3: Order Placement & Structured Error Handling Tests
    // ==========================================

    @Test
    @DisplayName("Task 3: POST /v1/orders - Happy Path: Place order with sufficient stock returns 201 Created")
    void createOrder_withSufficientStock_returns201() throws Exception {
        OrderRequestDTO request = OrderRequestDTO.builder()
                .userId("usr_1001")
                .items(List.of(
                        OrderItemRequestDTO.builder().bookId(dddaBookId).quantity(1).build()
                ))
                .shippingAddress(AddressDTO.builder()
                        .street("456 Tech Park Way")
                        .city("Pune")
                        .state("Maharashtra")
                        .postalCode("411057")
                        .country("India")
                        .build())
                .paymentMethodId("pm_card_visa_4242")
                .build();

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.orderId", startsWith("ord_")))
                .andExpect(jsonPath("$.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.totalAmount", is(45.00)))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    @DisplayName("Task 3: POST /v1/orders - Unhappy Path: Quantity exceeds stock returns 422 Unprocessable Entity")
    void createOrder_whenQuantityExceedsStock_returns422() throws Exception {
        // Book 4 (Spring Boot in Action) has only 2 copies in stock. We request 10.
        OrderRequestDTO request = OrderRequestDTO.builder()
                .userId("usr_1001")
                .items(List.of(
                        OrderItemRequestDTO.builder().bookId(springBootBookId).quantity(10).build()
                ))
                .shippingAddress(AddressDTO.builder()
                        .street("123 Main St")
                        .city("Pune")
                        .state("MH")
                        .postalCode("411001")
                        .country("India")
                        .build())
                .paymentMethodId("pm_card_visa")
                .build();

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.error", is("Unprocessable Entity")))
                .andExpect(jsonPath("$.code", is("INSUFFICIENT_STOCK")))
                .andExpect(jsonPath("$.field", is("items[0].quantity")))
                .andExpect(jsonPath("$.details.bookId", is(springBootBookId.intValue())))
                .andExpect(jsonPath("$.details.requestedQuantity", is(10)))
                .andExpect(jsonPath("$.details.availableStock", is(2)));
    }

    @Test
    @DisplayName("Task 3: GET /v1/users/{userId}/orders - Retrieves all orders for specific user with 200 OK")
    void getUserOrders_returns200() throws Exception {
        // Place an order first
        OrderRequestDTO request = OrderRequestDTO.builder()
                .userId("usr_1001")
                .items(List.of(
                        OrderItemRequestDTO.builder().bookId(dddaBookId).quantity(1).build()
                ))
                .shippingAddress(AddressDTO.builder()
                        .street("456 Tech Park Way")
                        .city("Pune")
                        .state("Maharashtra")
                        .postalCode("411057")
                        .country("India")
                        .build())
                .paymentMethodId("pm_card_visa_4242")
                .build();

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/users/usr_1001/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is("usr_1001")));
    }
}
