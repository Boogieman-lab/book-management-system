package com.example.bookmanagementsystembo.bookrequest.dto;

public record BookRequestCreateReq(String title, String authors, String publisher, String isbn, String reason) {
}
