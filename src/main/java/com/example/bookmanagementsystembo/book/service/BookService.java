package com.example.bookmanagementsystembo.book.service;

import com.example.bookmanagementsystembo.book.dto.BookDto;
import com.example.bookmanagementsystembo.book.entity.Book;
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
    public BookDto getBookById(Long bookId) {
        Book book =  bookRepository.findById(bookId)
                .orElseThrow(() -> new CoreException(ErrorType.BOOK_NOT_FOUND, bookId));
        return BookDto.from(book);
    }


}
