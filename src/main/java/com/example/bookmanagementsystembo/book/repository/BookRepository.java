package com.example.bookmanagementsystembo.book.repository;

import com.example.bookmanagementsystembo.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn13(String isbn13);
    List<Book> findByTitleContaining(String title);
    List<Book> findByPublisherContaining(String query);
    List<Book> findByAuthorContaining(String query);
    boolean existsByIsbn13(String isbn13);
}
