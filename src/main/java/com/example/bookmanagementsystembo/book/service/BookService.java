package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookCreateReq;
import com.example.bookmanagementsystembo.book.dto.BookUpdateReq;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.bookHold.entity.BookHold;
import com.example.bookmanagementsystembo.book.enums.BookSearchField;
import com.example.bookmanagementsystembo.bookHold.repository.BookHoldRepository;
import com.example.bookmanagementsystembo.book.repository.BookRepository;
import com.example.bookmanagementsystembo.book.dto.BookRes;
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

    public BookRes read(Long bookId) {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        return BookRes.from(book);
    }

    @Transactional
    public BookRes create(BookCreateReq request) {

        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(Book.create(request)));

        bookHoldRepository.save(BookHold.create(book.getBookId()));

        return BookRes.from(book);
    }

    @Transactional
    public BookRes update(Long bookId, BookUpdateReq request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        book.update(request);
        return BookRes.from(book);
    }

    @Transactional
    public void delete(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));

        bookRepository.delete(book);
        bookHoldRepository.deleteByBookId(bookId);
    }

    public List<BookRes> searchBooks(BookSearchField field, String query) {
        return switch (field) {
            case AUTHOR -> getBooksByAuthor(query);
            case PUBLISHER -> getBooksByPublisher(query);
            case ISBN -> getBooksByIsbn(query);
            case TITLE -> getBooksByTitle(query);
        };
    }

    private List<BookRes> getBooksByTitle(String title) {
        return bookRepository.findByTitleContaining(title)
                .stream()
                .map(BookRes::from)
                .toList();
    }


    private List<BookRes> getBooksByIsbn(String query) {
        return bookRepository.findByIsbn(query)
                .stream()
                .map(BookRes::from)
                .toList();
    }

    private List<BookRes> getBooksByPublisher(String query) {
        return bookRepository.findByPublisherContaining(query)
                .stream()
                .map(BookRes::from)
                .toList();
    }

    private List<BookRes> getBooksByAuthor(String query) {
        return bookRepository.findByAuthorsContaining(query)
                .stream()
                .map(BookRes::from)
                .toList();
    }
}
