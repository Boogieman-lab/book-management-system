package com.example.bookmanagementsystembo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    USER_NOT_FOUND(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.", LogLevel.WARN),
    USER_ALREADY_EXISTS(ErrorCode.CONFLICT, "존재하는 사용자 아이디 입니다.", LogLevel.WARN),
    NEW_PASSWORD_MISMATCH(ErrorCode.BAD_REQUEST, "새 비밀번호가 일치하지 않습니다.", LogLevel.WARN),

    FIELD_ERROR_DEFAULT(ErrorCode.BAD_REQUEST,"유효성 검사 오류입니다.", LogLevel.WARN),
    INTERNAL_SERVER_ERROR(ErrorCode.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", LogLevel.ERROR),

    BOOK_NOT_FOUND(ErrorCode.NOT_FOUND, "도서를 찾을 수 없습니다.", LogLevel.WARN),

    BOOK_EXTERNAL_SERVICE_ERROR(ErrorCode.BAD_GATEWAY, "도서 외부 서비스 호출이 실패했습니다.", LogLevel.WARN),

    DEPARTMENT_NOT_FOUND(ErrorCode.NOT_FOUND, "부서를 찾을 수 없습니다.", LogLevel.WARN),
    DEPARTMENT_ALREADY_EXISTS(ErrorCode.CONFLICT, "존재하는 부서 이름 입니다.", LogLevel.WARN),

    BOOKBORROW_NOT_FOUND(ErrorCode.NOT_FOUND, "대출 도서를 찾을 수 없습니다.", LogLevel.WARN),
    BORROWSTATUS_NOT_FOUND(ErrorCode.NOT_FOUND, "대출 상태 값을 찾을 수 없습니다.", LogLevel.WARN),

    REFRESH_TOKEN_EXPIRED(ErrorCode.UNAUTHORIZED, "만료된 토큰입니다.", LogLevel.WARN),
    TOKEN_NOT_FOUND(ErrorCode.NOT_FOUND, "토큰을 찾을 수 없습니다.", LogLevel.WARN),
    JSON_PROCESSING_ERROR(ErrorCode.BAD_REQUEST, "JSON 변환 오류입니다.", LogLevel.WARN);

    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;
}
