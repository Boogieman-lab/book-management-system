package com.example.bookmanagementsystembo.book.dto;

/** 재고 추가(+1) 요청 - 도서 실물이 새로 도착했을 때 사용 */
public record BookHoldAddReq(String location) {}
