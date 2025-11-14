package com.example.bookmanagementsystembo.book.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BookSearchField {
    AUTHOR, PUBLISHER, ISBN, TITLE;

    public static BookSearchField from(String value) {
        return BookSearchField.valueOf(value.toUpperCase());
    }
}
