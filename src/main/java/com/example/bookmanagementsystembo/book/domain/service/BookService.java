package com.example.bookmanagementsystembo.book.domain.service;

import com.example.bookmanagementsystembo.book.domain.dto.BookCreateDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookUpdateDto;
import com.example.bookmanagementsystembo.book.domain.entity.Book;
import com.example.bookmanagementsystembo.book.domain.entity.BookHold;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.book.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.example.bookmanagementsystembo.book.presentation.dto.BookResponse;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public void updateBook(BookUpdateDto request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, request.bookId()));

        book.update(request);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        bookRepository.delete(book);
        bookHoldRepository.deleteByBookId(bookId);
    }

    public List<BookDto> getBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    // Service
    public List<BookResponse> searchBooks(BookSearchField field, String query) {
        List<BookDto> books = switch (field) {
            case AUTHOR -> getBooksByAuthor(query);
            case PUBLISHER -> getBooksByPublisher(query);
            case ISBN -> getBooksByIsbn(query);
            case TITLE -> getBooksByTitle(query);
        };
        return BookResponse.from(books);
    }

    private List<BookDto> getBooksByIsbn(String query) {
        return bookRepository.findByIsbn(query)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    private List<BookDto> getBooksByPublisher(String query) {
        return bookRepository.findByPublisherContaining(query)
                .stream()
                .map(BookDto::from)
                .toList();
    }

    private List<BookDto> getBooksByAuthor(String query) {
        return bookRepository.findByAuthorsContaining(query)
                .stream()
                .map(BookDto::from)
                .toList();
    }
}
