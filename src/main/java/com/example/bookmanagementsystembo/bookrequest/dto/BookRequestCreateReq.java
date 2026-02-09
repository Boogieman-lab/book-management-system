package com.example.bookmanagementsystembo.bookrequest.dto;

public record BookRequestCreateReq(Long userId, String title, String authors, String publisher, String isbn, String reason) {
}
