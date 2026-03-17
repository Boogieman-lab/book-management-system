package com.example.bookmanagementsystembo.bookrequest.dto;

public record BookRequestUpdateReq(String title, String authors, String publisher, String isbn, String reason) {
}
