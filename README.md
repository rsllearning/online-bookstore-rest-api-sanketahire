# 📚 Online Bookstore System — RESTful API Backend

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Swagger OpenAPI 3.0](https://img.shields.io/badge/OpenAPI-3.0-blue.svg)](http://localhost:8080/swagger-ui.html)
[![Tests Passing](https://img.shields.io/badge/Tests-11%2F11%20Passed-success.svg)]()

A production-grade RESTful API backend for an **Online Bookstore System** built with **Java 21**, **Spring Boot 3.3**, **Spring Data JPA**, **H2 In-Memory Database**, and **OpenAPI/Swagger UI**.

---

## 📌 Master Assignment Deliverables

> ### 📄 **[CLICK HERE TO VIEW THE COMPLETE MASTER ASSIGNMENT SUBMISSION (ASSIGNMENT_SUBMISSION.md)](./ASSIGNMENT_SUBMISSION.md)**
> * **Task 1**: Complete Request Lifecycle & "Add to Cart" Application Layer Responsibilities.
> * **Task 2**: The completed 7-operation RESTful Endpoint Design Matrix & Principles.
> * **Task 3**: JSON Payloads for Order Placement (`201 Created`) and Structured Error Handling (`422 Unprocessable Entity`).
> * **Empirical Evidence**: Real-time cURL command logs, raw JSON outputs, and automated test results.

---

## 🏛️ System Architecture

```
                               ┌─────────────────────────────────────────┐
                               │       Client / Browser / Postman        │
                               └────────────────────┬────────────────────┘
                                                    │  HTTPS (Port 443 / 8080)
                                                    ▼
                               ┌─────────────────────────────────────────┐
                               │         Spring Boot Web Server          │
                               ├─────────────────────────────────────────┤
                               │ • Authentication / JWT Filter           │
                               │ • Input Validation & Global Error Hndlr │
                               └────────────────────┬────────────────────┘
                                                    │
                                                    ▼
                               ┌─────────────────────────────────────────┐
                               │        Application / Service Layer      │
                               ├─────────────────────────────────────────┤
                               │ • CartService (Add-to-Cart Lifecycle)   │
                               │ • BookService (Inventory Management)    │
                               │ • OrderService (Order Processing & Rule)│
                               └────────────────────┬────────────────────┘
                                                    │
                                                    ▼
                               ┌─────────────────────────────────────────┐
                               │         Database & Storage Layer        │
                               ├─────────────────────────────────────────┤
                               │ • Spring Data JPA + H2 In-Memory DB     │
                               │ • Tables: Books, Orders, Order_Items    │
                               └─────────────────────────────────────────┘
```

---

## 🚀 Quickstart & Interactive Testing

### 1. Start the Backend Server
```bash
./mvnw spring-boot:run
```
The application will start on `http://localhost:8080`.

### 2. Interactive Swagger UI (Browser Testing)
Visit: **`http://localhost:8080/swagger-ui.html`**
* Test all endpoints visually with 1 click.
* View interactive request/response schemas and live responses.

### 3. H2 In-Memory Database Console
Visit: **`http://localhost:8080/h2-console`**
* **JDBC URL**: `jdbc:h2:mem:bookstoredb`
* **User Name**: `sa`
* **Password**: *(leave blank)*

### 4. Run Automated Integration Tests (JUnit 5 + MockMvc)
```bash
./mvnw test
```

### 5. Postman Collection
Import [`bookstore_collection.json`](./bookstore_collection.json) directly into Postman to run pre-built requests.

---

## 📂 Project Structure

```
online-bookstore-api/
├── ASSIGNMENT_SUBMISSION.md         <-- Complete Master Submission (Tasks 1, 2, 3)
├── README.md                        <-- Project quickstart & architecture overview
├── bookstore_collection.json        <-- Pre-configured Postman collection
├── docs/screenshots/                <-- Visual evidence & UI captures
├── pom.xml                          <-- Maven build configuration (Java 21, Spring Boot 3)
└── src/
    ├── main/java/com/bookstore/
    │   ├── BookstoreApplication.java
    │   ├── config/
    │   │   ├── OpenApiConfig.java   (Swagger metadata & JWT Bearer scheme)
    │   │   └── DataInitializer.java (Pre-seeds 5 technical books)
    │   ├── controller/
    │   │   ├── BookController.java  (Task 2: 5 Books endpoints)
    │   │   ├── OrderController.java (Task 2 & 3: 2 Orders endpoints)
    │   │   └── CartController.java  (Task 1: Add to Cart endpoint)
    │   ├── dto/
    │   │   ├── BookRequestDTO.java
    │   │   ├── BookPriceUpdateDTO.java
    │   │   ├── BookResponseDTO.java
    │   │   ├── OrderRequestDTO.java
    │   │   ├── OrderResponseDTO.java
    │   │   └── ErrorResponseDTO.java (Task 3: Structured Error Envelope)
    │   ├── entity/
    │   │   ├── Book.java
    │   │   ├── Order.java
    │   │   ├── OrderItem.java
    │   │   └── CartItem.java
    │   ├── exception/
    │   │   ├── InsufficientStockException.java
    │   │   ├── ResourceNotFoundException.java
    │   │   └── GlobalExceptionHandler.java (Task 3: Centralized Error Handler)
    │   ├── repository/
    │   │   ├── BookRepository.java
    │   │   ├── OrderRepository.java
    │   │   └── CartItemRepository.java
    │   └── service/
    │       ├── BookService.java
    │       ├── OrderService.java
    │       └── CartService.java
    └── test/java/com/bookstore/
        └── BookstoreApiIntegrationTests.java (11 Integration Tests - 100% Pass)
```
