package com.example.bookmanagementsystembo.book.domain.service;

import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.domain.entity.Book;
import com.example.bookmanagementsystembo.book.domain.entity.BookHold;
import com.example.bookmanagementsystembo.book.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookHoldRepository bookHoldRepository;

    public BookDto getBookById(Long bookId) {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        return BookDto.from(book);
    }

    @Transactional
    public Long createBook(BookCreateDto request) {

        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(Book.create(request)));


        BookHold bookHold = BookHold.create(book.getBookId());
        BookHold savedBookHold = bookHoldRepository.save(bookHold);

        return savedBookHold.getBookHoldId();
    }
}
