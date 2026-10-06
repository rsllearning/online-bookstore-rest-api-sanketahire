# 📖 Online Bookstore System — RESTful API Assignment Submission

**Candidate**: Sanket Ahire  
**Assignment**: Intro To Backend, What's API And RESTful API  
**Organization**: Raja Software Labs  
**Repository**: [online-bookstore-rest-api-sanketahire](https://github.com/rsllearning/online-bookstore-rest-api-sanketahire.git)  
**Branch**: `feature/bookstore-rest-api`  

---

## 📑 Table of Contents
1. [Executive Summary & Problem Statement](#-executive-summary--problem-statement)
2. [Task 1: Backend Architecture & Request Lifecycle](#-task-1-backend-architecture--request-lifecycle)
   - 1.1 Complete End-to-End Request Journey
   - 1.2 Responsibilities of the Application / Business Logic Layer
   - 1.3 Concrete Implementation in Java / Spring Boot
3. [Task 2: RESTful Endpoint Design](#-task-2-restful-endpoint-design)
   - 2.1 Completed Design Table (7 Core Operations)
   - 2.2 Deep Dive on Design Decisions & Constraints
4. [Task 3: Data Exchange & JSON Payloads](#-task-3-data-exchange--json-payloads)
   - 3.1 Successful Order Creation (`201 Created`)
   - 3.2 Error Handling Scenario (`422 Unprocessable Entity`)
   - 3.3 Centralized Error Contract Architecture
5. [Empirical Verification & Live Test Evidence](#-empirical-verification--live-test-evidence)
6. [Quickstart & Verification Guide](#-quickstart--verification-guide)

---

## 🎯 Executive Summary & Problem Statement

This submission provides the architectural design, formal REST specification, structured JSON data exchange contracts, and a production-grade Java 21 / Spring Boot 3 implementation for an **Online Bookstore System**.

The project fulfills all requirements across three core tasks:
* **Task 1**: Deconstructing the full-stack request lifecycle and application layer responsibilities when a user executes **"Add to Cart"**.
* **Task 2**: Engineering 7 resource-oriented RESTful endpoints for Inventory (Books) and Purchases (Orders) adhering to standard HTTP semantics, Safe/Idempotent properties, and `/v1/` versioning.
* **Task 3**: Defining strict JSON request/response schemas for Order Creation and structured, field-level error handling for failure scenarios.

---

## 🚀 Task 1: Backend Architecture & Request Lifecycle

### Scenario
A user browses the online bookstore frontend and clicks the **"Add to Cart"** button for a book (*Clean Code*, Book ID: `1`, Quantity: `2`).

---

### 1.1 Complete End-to-End Request Journey

The interaction follows a deterministic path across physical and logical networking layers, from the client's browser across the internet to the server and back:

```
┌──────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   END-TO-END REQUEST LIFECYCLE                                   │
└────────────────────────────────────────────────┬─────────────────────────────────────────────────┘
                                                 │
  [1] USER ACTION                                ▼
  User clicks "Add to Cart" in React/Mobile app
  Browser prepares HTTP Request: POST /v1/cart/items
                                                 │
  [2] DNS RESOLUTION                             ▼
  Browser cache -> OS cache -> Resolving DNS Server -> Root/TLD/Authoritative Nameserver
  api.bookstore.com  ===>  Resolved to IP: 142.250.72.14
                                                 │
  [3] TRANSPORT & SECURITY                       ▼
  TCP 3-Way Handshake (SYN -> SYN-ACK -> ACK) on Port 443
  TLS 1.3 Cryptographic Handshake: Negotiates cipher suites, validates SSL/TLS Certificate,
  exchanges ephemeral session keys (Perfect Forward Secrecy)
                                                 │
  [4] HTTP PAYLOAD ENCRYPTION & TRANSIT          ▼
  Browser serializes JSON & encrypts request:
  POST /v1/cart/items HTTP/1.1
  Host: api.bookstore.com
  Authorization: Bearer eyJhbGciOi...
  Content-Type: application/json
  Body: {"bookId": 1, "quantity": 2}
                                                 │
  [5] SERVER / REVERSE PROXY LAYER               ▼
  Web Server / Load Balancer (e.g. Nginx, AWS ALB) terminates TLS, validates SSL,
  inspects port 443, and forwards cleartext HTTP stream to Spring Boot embedded Tomcat on port 8080
                                                 │
  [6] APPLICATION & BUSINESS LOGIC LAYER         ▼
  • Security Filter: Validates JWT signature & extracts userId (e.g. usr_1001)
  • Controller Routing: Maps POST /v1/cart/items to CartController.addItemToCart()
  • Input Validation: Validates bookId not null, quantity >= 1
  • Inventory Query: Calls BookRepository.findById(1)
  • Business Rule Evaluation: Verifies stock (15 >= 2) and enforces max cart limit (<= 10)
  • State Mutation: Updates CartItem record in database
                                                 │
  [7] DATABASE LAYER                             ▼
  Relational Database (H2 / PostgreSQL) executes SQL transaction:
  SELECT * FROM books WHERE id = 1;
  INSERT INTO cart_items (user_id, book_id, quantity, added_at) VALUES ('usr_1001', 1, 2, NOW());
  Transaction COMMITTED.
                                                 │
  [8] RESPONSE GENERATION & RETURN TRIP          ▼
  Application builds CartResponseDTO with calculated subtotal ($69.98).
  Tomcat serializes JSON and sends HTTP 200 OK to Reverse Proxy.
  Reverse Proxy encrypts via TLS and transmits packets across TCP to client.
                                                 │
  [9] CLIENT RENDERING                           ▼
  Browser receives HTTP 200 OK, decrypts payload, and updates the UI cart badge from 0 to 2.
```

---

#### Detailed Breakdown of Component Roles:

#### 1. DNS Lookup and IP Addresses
* **The Problem**: Humans navigate using readable domain names (`api.bookstore.com`), but network routing protocols (IP/BGP) operate strictly on numerical IP addresses (e.g., IPv4: `142.250.72.14` or IPv6: `2607:f8b0:4005:805::200e`).
* **The Process**:
  1. The browser checks its local DNS cache.
  2. If missing, it queries the Operating System resolver (`/etc/hosts`, local DNS client).
  3. If unresolved, the request queries the Recursive DNS Resolver (ISP or `8.8.8.8`).
  4. The resolver iteratively queries the Root Nameserver (`.`), the TLD Nameserver (`.com`), and finally the Authoritative Nameserver for `bookstore.com`.
  5. The DNS `A` (Address) record is returned to the client and cached according to its **TTL (Time to Live)**.

#### 2. Standard HTTP/HTTPS Protocols and Ports
* **Port Allocation**:
  * **Port `80`**: Default for unencrypted HTTP communication.
  * **Port `443`**: Standard dedicated port for HTTPS (HTTP over TLS/SSL).
  * **Port `8080` / `8443`**: Standard internal port for application servers / local development.
* **HTTPS Security Layer**:
  * **Confidentiality**: Request headers (including sensitive `Authorization: Bearer <token>` and session cookies) and request payload (`{"bookId": 1, "quantity": 2}`) are encrypted end-to-end, preventing packet sniffing across public Wi-Fi or transit routers.
  * **Integrity**: Cryptographic message authentication codes (HMAC) guarantee that no intermediary alters the payload (e.g., changing quantity or destination).
  * **Authentication**: The server presents a digital certificate signed by a trusted Certificate Authority (CA), assuring the browser that it is truly communicating with the legitimate bookstore backend.

#### 3. Server, Application/Business Logic Layer, and Database
* **Server (Web Server / Reverse Proxy)**:
  * Manages OS-level socket listeners, TCP connections, TLS handshakes, rate limiting, and HTTP request parsing before routing requests to worker threads.
* **Application / Business Logic Layer**:
  * The computational brain of the backend. It decouples interface concerns from storage, parses incoming DTOs, validates inputs, enforces domain rules (e.g., pricing, discounts, eligibility), coordinates multi-step workflows, and orchestrates database transactions.
* **Database (Storage Layer)**:
  * Provides durable, ACID-compliant persistence for entity models (`books`, `orders`, `cart_items`), ensuring data survives server restarts and concurrent multi-user transactions.

---

### 1.2 Specific Responsibilities of the Application / Business Logic Layer

During the **"Add to Cart"** interaction, the Application Layer executes five distinct, non-negotiable responsibilities:

```
  ┌────────────────────────────────────────────────────────────────────────┐
  │         APPLICATION / BUSINESS LOGIC LAYER RESPONSIBILITIES            │
  └───────────────────────────────────┬────────────────────────────────────┘
                                      │
  1. Authentication & Authorization   ▼
     • Extracts 'Authorization: Bearer <token>' header.
     • Validates cryptographic signature, expiry (exp), and extracts subject (userId).
     • Ensures user possesses active customer role.
                                      │
  2. Input Validation & Sanitization  ▼
     • Verifies 'bookId' is not null and is a valid positive integer.
     • Asserts 'quantity >= 1' (rejects 0, negative values, and non-integer floats).
     • Rejects malformed JSON syntax with 400 Bad Request before hitting DB.
                                      │
  3. Real-Time Inventory Check        ▼
     • Queries BookRepository for real-time stock levels.
     • Validates that available stock >= (current_cart_quantity + requested_quantity).
     • If stock is insufficient, halts execution and raises InsufficientStockException.
                                      │
  4. Domain Business Rules Execution  ▼
     • Enforces cart policy: Maximum 10 copies per title per user.
     • Dynamically calculates item subtotal: unitPrice * quantity.
     • Recalculates total cart subtotal and applicable promotional discounts.
                                      │
  5. Persistence & Response Contract  ▼
     • Executes ACID-compliant transaction: inserts or updates CartItem in DB.
     • Builds standardized JSON response payload with HTTP 200 OK status code.
```

---

### 1.3 Concrete Implementation in Java / Spring Boot

The theoretical lifecycle described above is implemented in our codebase inside **`CartController.java`** and **`CartService.java`**:

```java
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Transactional
    public CartResponseDTO addItemToCart(String userId, CartItemRequestDTO request) {
        // 1. Validate Book Existence
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));

        // 2. Fetch Existing Cart State
        CartItem existingItem = cartItemRepository.findByUserIdAndBookId(userId, request.getBookId()).orElse(null);
        int currentCartQty = (existingItem != null) ? existingItem.getQuantity() : 0;
        int newTotalQty = currentCartQty + request.getQuantity();

        // 3. Enforce Business Rules (Max 10 per title)
        if (newTotalQty > 10) {
            throw new IllegalArgumentException("Cart limit exceeded. Maximum 10 units allowed per title.");
        }

        // 4. Verify Stock Availability
        if (book.getStockQuantity() < newTotalQty) {
            throw new InsufficientStockException(book.getId(), newTotalQty, book.getStockQuantity());
        }

        // 5. Persist State
        if (existingItem != null) {
            existingItem.setQuantity(newTotalQty);
            cartItemRepository.save(existingItem);
        } else {
            cartItemRepository.save(CartItem.builder()
                    .userId(userId)
                    .bookId(book.getId())
                    .quantity(request.getQuantity())
                    .build());
        }

        // 6. Return Fresh Cart Snapshot
        return getCartSnapshot(userId);
    }
}
```


---

## 🛠️ Task 2: RESTful Endpoint Design

### 2.1 Completed Design Table

Below is the complete RESTful endpoint design table fulfilling all 7 required operations for managing bookstore inventory (Books) and user purchases (Orders), adhering strictly to resource-based URIs, lowercase plural nouns, HTTP verb semantics, Safe/Idempotent properties, and `/v1/` versioning.

| Requirement / Operation | HTTP Method | Endpoint (URL Path) | Safe? (Yes/No) | Idempotent? (Yes/No) | Brief Description |
| :--- | :---: | :--- | :---: | :---: | :--- |
| **Fetch list of all books** | `GET` | `/v1/books` | **Yes** | **Yes** | Retrieves a paginated/filtered list of all books in the inventory. Read-only operation. |
| **Fetch details of a specific book by ID** | `GET` | `/v1/books/{bookId}` | **Yes** | **Yes** | Retrieves complete details for a single book identified by `bookId`. Returns `404` if not found. |
| **Add a new book to the inventory** | `POST` | `/v1/books` | **No** | **No** | Creates a new book record. Returns `201 Created` with the newly created book and `Location` header. |
| **Update price of an existing book** | `PATCH` | `/v1/books/{bookId}` | **No** | **No** *(Not Guaranteed)* | Partially updates only the `price` field of the specified book resource without altering other attributes. |
| **Delete a book record from inventory** | `DELETE` | `/v1/books/{bookId}` | **No** | **Yes** | Permanently removes the book record identified by `bookId` from the inventory. Returns `204 No Content`. |
| **Place a new book order** | `POST` | `/v1/orders` | **No** | **No** | Submits a new purchase order, validates inventory, decrements stock, and returns `201 Created`. |
| **Retrieve all orders belonging to a specific user** | `GET` | `/v1/users/{userId}/orders` | **Yes** | **Yes** | Retrieves the full historical list of purchase orders placed by the user identified by `userId`. |

---

### 2.2 Deep Dive on Design Decisions & Constraints

#### 1. Resource-Based Naming vs. Verb-Based Anti-Patterns
* **REST Principle**: Endpoints must represent **resources (nouns)**, not operational actions (verbs).
* **Correct**: `POST /v1/books` (HTTP verb indicates creation, URL indicates the target collection).
* **Anti-Pattern (Avoided)**: `POST /v1/addBook`, `GET /v1/getBooks`, `POST /v1/deleteBookById`.

#### 2. Path Parameters vs. Query Parameters
* **Path Parameters (`/v1/books/{bookId}`)**: Used to target an **unambiguous, individual resource instance** within a collection (e.g., Book ID `1` or User ID `usr_1001`).
* **Query Parameters (`/v1/books?category=Software+Engineering&page=1&limit=10`)**: Used to **filter, sort, search, or paginate** across a collection without changing the resource identity.

#### 3. Formal Definitions: Safe vs. Idempotent
* **Safe**:
  * An HTTP method is **Safe** if it does **not alter any server-side state or data**.
  * `GET` is safe because calling it 1,000 times only retrieves data without mutating records.
  * `POST`, `PUT`, `PATCH`, and `DELETE` are **unsafe** because they mutate database records.
* **Idempotent**:
  * An HTTP method is **Idempotent** if executing $N$ identical requests produces the **exact same final server state** as executing $1$ request ($f(f(x)) = f(x)$).
  * `GET` is idempotent: Repeated calls leave data unchanged.
  * `DELETE` is idempotent: Deleting `/v1/books/1` the first time deletes the book; deleting it a second time leaves the book deleted.
  * `PUT` is idempotent: Completely replacing an object with the same state produces the identical result every time.
  * `PATCH` is **not guaranteed to be idempotent**: E.g., if a patch increments a price (`{"increment": 5}`), repeated executions would continuously raise the price.
  * `POST` is **neither safe nor idempotent**: Executing `POST /v1/orders` 5 times will create 5 separate purchase orders with 5 distinct IDs.

#### 4. Hierarchical Resource Nesting
* For relationships where a child entity naturally belongs to a parent context, sub-resource routing is used:
  * `/v1/users/{userId}/orders` explicitly expresses: *"Retrieve the order sub-collection belonging to user {userId}."*

#### 5. API Versioning Strategy (`/v1/...`)
* Prefixing the URL path with `/v1/` establishes a clear **API contract**. If breaking schema changes occur in the future, `/v2/` can be introduced alongside `/v1/` without breaking legacy mobile apps or partner integrations.


---

## 📦 Task 3: Data Exchange & JSON Payloads

For the **"Place a new book order"** (`POST /v1/orders`) operation designed in Task 2, the HTTP request and response structures are formally defined below for both success and failure scenarios.

---

### 3.1 Successful Order Creation (`201 Created`)

#### 1. Required HTTP Request Headers
```http
POST /v1/orders HTTP/1.1
Host: api.bookstore.com
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3JfMTAwMSIsIm5hbWUiOiJTYW5rZXQgQWhpcmUiLCJyb2xlIjoiQ1VTVE9NRVIiLCJpYXQiOjE3MjgyMjQ0MDB9...
```

* **`Content-Type: application/json`**: Informs the server that the request body is formatted as a JSON payload.
* **`Accept: application/json`**: Signals to the server that the client expects a JSON response body.
* **`Authorization: Bearer <token>`**: Carries the client's cryptographically signed JWT token for stateless authentication and authorization.

---

#### 2. Request JSON Body (`POST /v1/orders`)
```json
{
  "userId": "usr_1001",
  "items": [
    {
      "bookId": 1,
      "quantity": 2
    },
    {
      "bookId": 4,
      "quantity": 1
    }
  ],
  "shippingAddress": {
    "street": "456 Tech Park Way, Floor 4",
    "city": "Pune",
    "state": "Maharashtra",
    "postalCode": "411057",
    "country": "India"
  },
  "paymentMethodId": "pm_card_visa_4242"
}
```

---

#### 3. Expected Successful JSON Response Body (`201 Created`)

```http
HTTP/1.1 201 Created
Location: /v1/orders/ord_1728224400_9182
Content-Type: application/json
Date: Tue, 06 Oct 2026 14:45:00 GMT

{
  "orderId": "ord_1728224400_9182",
  "userId": "usr_1001",
  "status": "CONFIRMED",
  "totalAmount": 99.97,
  "shippingAddress": "456 Tech Park Way, Floor 4, Pune, Maharashtra 411057, India",
  "paymentMethodId": "pm_card_visa_4242",
  "items": [
    {
      "bookId": 1,
      "bookTitle": "Clean Code",
      "unitPrice": 34.99,
      "quantity": 2,
      "subtotal": 69.98
    },
    {
      "bookId": 4,
      "bookTitle": "Spring Boot in Action",
      "unitPrice": 29.99,
      "quantity": 1,
      "subtotal": 29.99
    }
  ],
  "createdAt": "2026-10-06T14:45:00.124Z"
}
```

---

### 3.2 Error Handling Scenario: Insufficient Inventory Stock

#### 1. Failure Scenario Description
* A customer submits an order requesting **`10` copies** of *Spring Boot in Action* (Book ID: `4`).
* The system performs an inventory lookup and discovers that the current stock count in the database is only **`2` copies**.
* Because the purchase order cannot be completely fulfilled, the Application Layer halts the transaction, rolls back any partial database state, and generates a structured error response.

---

#### 2. HTTP Status Code Selection: `422 Unprocessable Entity`
* **Why not `400 Bad Request`?**: The request payload is syntactically valid JSON and adheres to all structural type rules (integers, strings, and non-empty arrays).
* **Why not `500 Internal Server Error`?**: Insufficient inventory is an expected business domain condition, not an unhandled backend crash.
* **Why `422 Unprocessable Entity`?**: Defined in RFC 4918 / RFC 9110, `422` indicates that the server understands the content type and syntax of the request, but is unable to process the contained business instructions due to semantic/domain validation errors.

---

#### 3. Structured JSON Error Response Body

```http
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/json
Date: Tue, 06 Oct 2026 14:46:12 GMT

{
  "timestamp": "2026-10-06T14:46:12.891Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "code": "INSUFFICIENT_STOCK",
  "message": "Requested quantity (10) exceeds available stock (2) for book ID 4.",
  "field": "items[0].quantity",
  "details": {
    "bookId": 4,
    "bookTitle": "Spring Boot in Action",
    "requestedQuantity": 10,
    "availableStock": 2
  },
  "path": "/v1/orders"
}
```

---

### 3.3 Centralized Error Contract Architecture

As emphasized in Module 3.6 of the training guide (*"Consistency pays off: pick one shape for your successful responses and one for your errors"*), our backend enforces a uniform error envelope across the entire API:

1. **`timestamp`**: Accurate UTC ISO-8601 timestamp for distributed log correlation.
2. **`status` & `error`**: Standard HTTP numerical code and canonical reason phrase.
3. **`code`**: Stable, machine-readable application error code enabling frontend clients to trigger localized UI error messages or specific recovery flows.
4. **`message`**: Clear, human-readable description explaining the exact cause of failure.
5. **`field`**: Pinpoints the exact JSON key or array index responsible for the error.
6. **`details`**: Context-rich debugging metadata without exposing internal database schemas.
7. **Security Guardrail**: Unhandled exceptions (`500`) are intercepted by `@RestControllerAdvice` to ensure raw Java stack traces, database credentials, or class names are **never leaked** to callers.
