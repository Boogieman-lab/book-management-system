package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookDto;
import com.example.bookmanagementsystembo.book.entity.Book;
import com.example.bookmanagementsystembo.book.entity.BookHold;
import com.example.bookmanagementsystembo.book.infra.BookHoldRepository;
import com.example.bookmanagementsystembo.book.infra.BookRepository;
import com.example.bookmanagementsystembo.book.presentation.dto.BookCreateRequest;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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


    private final ObjectMapper objectMapper;

    @Transactional
    public Long createBook(BookCreateRequest request) {
        // 1. ISBN으로 Book 존재 여부 확인
        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> {
                    try {
                        // 2. 신규 도서일 경우 Book 생성
                        Book newBook = Book.create(
                                request.title(),
                                request.contents(),
                                request.isbn(),
                                request.datetime(),
                                objectMapper.writeValueAsString(request.authors()),
                                objectMapper.writeValueAsString(request.translators()),
                                request.publisher(),
                                request.price(),
                                request.salePrice(),
                                request.thumbnail(),
                                request.status(),
                                request.url()
                        );
                        return bookRepository.save(newBook);
                    } catch (JsonProcessingException e) {
                        throw new CoreException(ErrorType.BOOK_EXTERNAL_JSON_PROCESSING_ERROR, e);
                    }
                });

        // 3. BookHold 생성
        BookHold bookHold = BookHold.create(book.getBookId(), null);
        BookHold savedBookHold = bookHoldRepository.save(bookHold);

        return savedBookHold.getBookHoldId(); // <-- ID 추출
    }
}
