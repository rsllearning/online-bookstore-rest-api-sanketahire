package com.bookstore.service;

import com.bookstore.dto.BookPriceUpdateDTO;
import com.bookstore.dto.BookRequestDTO;
import com.bookstore.dto.BookResponseDTO;
import com.bookstore.entity.Book;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks(String category) {
        List<Book> books;
        if (category != null && !category.trim().isEmpty()) {
            books = bookRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            books = bookRepository.findAll();
        }
        return books.stream().map(BookResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return BookResponseDTO.fromEntity(book);
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO request) {
        bookRepository.findByIsbn(request.getIsbn()).ifPresent(b -> {
            throw new IllegalArgumentException("A book with ISBN " + request.getIsbn() + " already exists.");
        });

        Book newBook = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .build();

        Book savedBook = bookRepository.save(newBook);
        return BookResponseDTO.fromEntity(savedBook);
    }

    @Transactional
    public BookResponseDTO updateBookPrice(Long id, BookPriceUpdateDTO request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        book.setPrice(request.getPrice());
        Book updatedBook = bookRepository.save(book);
        return BookResponseDTO.fromEntity(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        bookRepository.delete(book);
    }
}
