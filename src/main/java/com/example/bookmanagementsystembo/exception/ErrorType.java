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

    DEPARTMENT_NOT_FOUND(ErrorCode.NOT_FOUND, "부서를 찾을 수 없습니다.", LogLevel.WARN),

    ;
    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;
}
