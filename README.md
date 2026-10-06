# 📚 Online Bookstore System — RESTful API Backend

A production-grade RESTful API backend for an **Online Bookstore System** built with **Java 21**, **Spring Boot 3.3**, **Spring Data JPA**, **H2 In-Memory Database**, and **OpenAPI/Swagger UI**.

This project fulfills the technical requirements for designing and documenting RESTful backend architectures, end-to-end request lifecycles, resource-oriented endpoint design, and structured JSON data exchange contracts.

---

## 🏛️ System Architecture Overview

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

## 🛠️ Technology Stack

* **Language**: Java 21 (OpenJDK)
* **Framework**: Spring Boot 3.3.4
* **Persistence**: Spring Data JPA & Hibernate
* **Database**: H2 In-Memory Relational Database
* **API Documentation**: Springdoc OpenAPI 3.0 & Swagger UI
* **Validation**: Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Min`)
* **Testing**: JUnit 5, MockMvc, AssertJ

---

## 📋 Repository Branch Structure

* **`main`**: Initial foundation, build configuration, and core project scaffolding.
* **`feature/bookstore-rest-api`**: Complete feature implementation covering:
  * **Task 1**: Backend Architecture & "Add to Cart" Request Lifecycle Implementation.
  * **Task 2**: 7 RESTful Endpoints for Books Inventory & User Orders.
  * **Task 3**: Structured JSON Data Exchange, DTOs, and Centralized Error Handling (`422 Unprocessable Entity`).
  * **Automated Tests**: Comprehensive JUnit 5 / MockMvc integration test suite for happy and unhappy paths.
  * **Documentation**: Exhaustive master assignment submission document (`ASSIGNMENT_SUBMISSION.md`) and live execution screenshots.

---

## 🚀 Quickstart & Running the Application

### 1. Prerequisites
* Java JDK 21+
* Git

### 2. Running Locally
```bash
./mvnw spring-boot:run
```
The server will start on port `8080`: `http://localhost:8080`.

### 3. Interactive Documentation (Swagger UI)
Once started, visit:
**`http://localhost:8080/swagger-ui.html`**

### 4. Database Console (H2)
* URL: **`http://localhost:8080/h2-console`**
* JDBC URL: `jdbc:h2:mem:bookstoredb`
* Username: `sa` | Password: *(blank)*
