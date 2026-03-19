package com.example.bookmanagementsystembo.bookBorrow.repository;

import com.example.bookmanagementsystembo.bookBorrow.dto.AdminBorrowRes;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookBorrowQueryRepository {
    List<BookBorrowDto> findBookBorrows();

    BookBorrowDetailDto findBookBorrow(Long bookBorrowId);

    Page<AdminBorrowRes> findAllForAdmin(BorrowStatus status, Pageable pageable);
}

