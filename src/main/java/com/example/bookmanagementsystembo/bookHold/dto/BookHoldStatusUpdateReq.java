package com.example.bookmanagementsystembo.bookHold.dto;

import com.example.bookmanagementsystembo.bookHold.enums.BookHoldStatus;
import jakarta.validation.constraints.NotNull;

/** 도서 보유본 상태 변경 요청 (관리자 전용) */
public record BookHoldStatusUpdateReq(@NotNull BookHoldStatus status) {}
