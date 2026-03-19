package com.example.bookmanagementsystembo.bookBorrow.controller;

import com.example.bookmanagementsystembo.bookBorrow.dto.AdminBorrowRes;
import com.example.bookmanagementsystembo.bookBorrow.dto.BorrowPageRes;
import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.bookBorrow.service.BookBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 대출 현황 조회 V1 API.
 *
 * GET /api/v1/admin/borrows — 대출 현황 목록 (페이지네이션)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/borrows")
public class AdminBorrowController {

    private final BookBorrowService bookBorrowService;

    /** 관리자 대출 현황 조회 */
    @GetMapping
    public ResponseEntity<BorrowPageRes> getAdminBorrows(
            @RequestParam(required = false) BorrowStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminBorrowRes> result = bookBorrowService.findAllForAdmin(status, PageRequest.of(page - 1, size));
        BorrowPageRes res = new BorrowPageRes(
                result.getContent(),
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
        return ResponseEntity.ok(res);
    }
}
