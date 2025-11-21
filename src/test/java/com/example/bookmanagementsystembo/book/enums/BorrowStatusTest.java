package com.example.bookmanagementsystembo.book.enums;

import com.example.bookmanagementsystembo.bookBorrow.enums.BorrowStatus;
import com.example.bookmanagementsystembo.exception.CoreException;
import com.example.bookmanagementsystembo.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class BorrowStatusTest {

    @Test
    @DisplayName("없는 상태값으로 비교했을 경우 실패")
    void fromString_fail(){
        // Given
        String value = "UNKNOWN_STATUS";
        // When & Then
        CoreException ex = assertThrows(CoreException.class, () -> BorrowStatus.fromString(value));
        assertEquals(ErrorType.BORROWSTATUS_NOT_FOUND, ex.getErrorType());
    }


    @Test
    @DisplayName("성공")
    void fromString_success(){
        // Given
        String input = "BORROWED";
        // When
        BorrowStatus result = BorrowStatus.fromString(input);
        // Then
        assertEquals(BorrowStatus.BORROWED, result);
    }

    @Test
    @DisplayName("소문자 입력 성공")
    void fromString_lowercase_success() {
        // Given
        String input = "returned";
        // When
        BorrowStatus result = BorrowStatus.fromString(input);
        // Then
        assertEquals(BorrowStatus.RETURNED, result);
    }
}