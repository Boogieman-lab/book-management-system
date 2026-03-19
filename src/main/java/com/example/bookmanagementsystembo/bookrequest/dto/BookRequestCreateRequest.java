package com.example.bookmanagementsystembo.bookRequest.dto;

public record BookRequestCreateRequest(String title, String authors, String publisher, String isbn, String reason) {
}
