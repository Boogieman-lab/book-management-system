package com.example.bookmanagementsystembo.bookBorrow.infra;

import com.example.bookmanagementsystembo.bookBorrow.domain.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.domain.dto.BookBorrowDto;

import java.util.List;

public interface BookBorrowQueryRepository {
    List<BookBorrowDto> findBookBorrows();

    BookBorrowDetailDto findBookBorrow(Long bookBorrowId);
}

