package com.example.bookmanagementsystembo.book.infra;

import com.example.bookmanagementsystembo.book.entity.BookBorrow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookBorrowRepository extends JpaRepository<BookBorrow, Long>, BookBorrowQueryRepository {
}
