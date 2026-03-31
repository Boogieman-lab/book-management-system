package com.example.bookmanagementsystembo.book.enums;

public enum BookSearchField {
    AUTHOR("저자"),
    PUBLISHER("출판사"),
    ISBN("ISBN"),
    TITLE("제목");

    private final String description;

    BookSearchField(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static BookSearchField from(String value) {
        return BookSearchField.valueOf(value.toUpperCase());
    }
}
