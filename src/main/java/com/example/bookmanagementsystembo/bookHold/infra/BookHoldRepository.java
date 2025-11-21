package com.example.bookmanagementsystembo.bookHold.infra;

import com.example.bookmanagementsystembo.bookHold.domain.entity.BookHold;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookHoldRepository extends JpaRepository<BookHold, Long> {
    void deleteByBookId(Long bookId);
}
