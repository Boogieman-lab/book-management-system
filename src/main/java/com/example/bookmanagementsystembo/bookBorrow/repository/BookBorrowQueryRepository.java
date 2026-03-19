package com.example.bookmanagementsystembo.bookBorrow.repository;

import com.example.bookmanagementsystembo.bookBorrow.dto.AdminBorrowSummaryResponse;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDetailDto;
import com.example.bookmanagementsystembo.bookBorrow.dto.BookBorrowDto;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.user.dto.UserBorrowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookBorrowQueryRepository {
    List<BookBorrowDto> findBookBorrows();

    BookBorrowDetailDto findBookBorrow(Long bookBorrowId);

    Page<AdminBorrowSummaryResponse> findAllForAdmin(BorrowStatus status, Pageable pageable);

    Page<UserBorrowResponse> findByUserId(Long userId, BorrowStatus status, Pageable pageable);
}

