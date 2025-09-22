package com.example.bookmanagementsystembo.book.infra;

import com.example.bookmanagementsystembo.book.domain.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.book.domain.dto.BookBorrowDto;

import java.util.List;

public interface BookBorrowQueryRepository {
    List<BookBorrowDto> findBookBorrows();

    BookBorrowDetailDto findBookBorrow(Long bookBorrowId);
}

