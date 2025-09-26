package com.example.bookmanagementsystembo.book.infra;

import com.example.bookmanagementsystembo.book.domain.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    void deleteByBookId(Book book);
    BookDto findBookbyId(Long bookId);
}