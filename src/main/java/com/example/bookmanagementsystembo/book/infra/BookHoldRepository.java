package com.example.bookmanagementsystembo.book.infra;

import com.example.bookmanagementsystembo.book.domain.entity.BookHold;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookHoldRepository extends JpaRepository<BookHold, Long> {
}
