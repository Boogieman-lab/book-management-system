package com.example.bookmanagementsystembo.book.infra;

import com.example.bookmanagementsystembo.book.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    void deleteByBookId(Book book);
}