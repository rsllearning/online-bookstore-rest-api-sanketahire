package com.bookstore.config;

import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            log.info("Seeding initial book inventory into H2 database...");

            List<Book> initialBooks = List.of(
                    Book.builder()
                            .title("Clean Code")
                            .author("Robert C. Martin")
                            .isbn("978-0132350884")
                            .price(new BigDecimal("34.99"))
                            .stockQuantity(15)
                            .category("Software Engineering")
                            .build(),
                    Book.builder()
                            .title("Designing Data-Intensive Applications")
                            .author("Martin Kleppmann")
                            .isbn("978-1449373320")
                            .price(new BigDecimal("45.00"))
                            .stockQuantity(20)
                            .category("Distributed Systems")
                            .build(),
                    Book.builder()
                            .title("The Pragmatic Programmer")
                            .author("Andrew Hunt & David Thomas")
                            .isbn("978-0135957059")
                            .price(new BigDecimal("39.99"))
                            .stockQuantity(8)
                            .category("Software Engineering")
                            .build(),
                    Book.builder()
                            .title("Spring Boot in Action")
                            .author("Craig Walls")
                            .isbn("978-1617292545")
                            .price(new BigDecimal("29.99"))
                            .stockQuantity(2) // Intentionally low stock to easily test 422 Insufficient Stock error
                            .category("Frameworks")
                            .build(),
                    Book.builder()
                            .title("Domain-Driven Design")
                            .author("Eric Evans")
                            .isbn("978-0321125217")
                            .price(new BigDecimal("49.99"))
                            .stockQuantity(12)
                            .category("Software Architecture")
                            .build()
            );

            bookRepository.saveAll(initialBooks);
            log.info("Successfully seeded {} books into database.", initialBooks.size());
        }
    }
}
