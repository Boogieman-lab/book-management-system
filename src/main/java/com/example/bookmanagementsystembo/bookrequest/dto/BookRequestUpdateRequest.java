package com.example.bookmanagementsystembo.bookRequest.dto;

public record BookRequestUpdateRequest(String title, String authors, String publisher, String isbn, String reason) {
}
