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
    INVALID_CREDENTIALS(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.", LogLevel.WARN),
    ACCOUNT_LOCKED(ErrorCode.FORBIDDEN, "로그인 5회 실패로 계정이 잠겼습니다. 관리자에게 문의하세요.", LogLevel.WARN),

    FIELD_ERROR_DEFAULT(ErrorCode.BAD_REQUEST,"유효성 검사 오류입니다.", LogLevel.WARN),
    INTERNAL_SERVER_ERROR(ErrorCode.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", LogLevel.ERROR),

    BOOK_NOT_FOUND(ErrorCode.NOT_FOUND, "도서를 찾을 수 없습니다.", LogLevel.WARN),

    BOOK_EXTERNAL_SERVICE_ERROR(ErrorCode.BAD_GATEWAY, "도서 외부 서비스 호출이 실패했습니다.", LogLevel.WARN),

    DEPARTMENT_NOT_FOUND(ErrorCode.NOT_FOUND, "부서를 찾을 수 없습니다.", LogLevel.WARN),
    DEPARTMENT_ALREADY_EXISTS(ErrorCode.CONFLICT, "존재하는 부서 이름 입니다.", LogLevel.WARN),

    BOOKBORROW_NOT_FOUND(ErrorCode.NOT_FOUND, "대출 도서를 찾을 수 없습니다.", LogLevel.WARN),
    BORROWSTATUS_NOT_FOUND(ErrorCode.NOT_FOUND, "대출 상태 값을 찾을 수 없습니다.", LogLevel.WARN),

    BOOK_HOLD_NOT_FOUND(ErrorCode.NOT_FOUND, "보유 도서를 찾을 수 없습니다.", LogLevel.WARN),
    BOOK_HOLD_CANNOT_CHANGE_BORROWED(ErrorCode.BAD_REQUEST, "대출 중인 도서는 직접 분실/폐기 처리할 수 없습니다. 반납 처리 후 상태를 변경해주세요.", LogLevel.WARN),

    BOOK_REQUEST_FOUND(ErrorCode.NOT_FOUND, "희망 도서를 찾을 수 없습니다.", LogLevel.WARN),


    TOKEN_INVALID(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.", LogLevel.WARN),
    TOKEN_NOT_FOUND(ErrorCode.NOT_FOUND, "토큰을 찾을 수 없습니다.", LogLevel.WARN),
    TOKEN_MISMATCH(ErrorCode.BAD_REQUEST,"토큰 일치하지 않습니다.", LogLevel.WARN),

    TOKEN_JTI_MISSING(ErrorCode.UNAUTHORIZED, "토큰 식별자가 없습니다.", LogLevel.WARN),
    TOKEN_BLACKLISTED(ErrorCode.UNAUTHORIZED, "로그아웃된 토큰입니다.", LogLevel.WARN),

    BOOK_NOT_AVAILABLE(ErrorCode.CONFLICT, "대출 가능한 상태가 아닙니다.", LogLevel.WARN),
    BORROW_LIMIT_EXCEEDED(ErrorCode.CONFLICT, "대출 한도(10권)를 초과했습니다.", LogLevel.WARN),
    BORROW_NOT_OWNER(ErrorCode.FORBIDDEN, "본인의 대출 건만 처리할 수 있습니다.", LogLevel.WARN),
    BORROW_ALREADY_RETURNED(ErrorCode.BAD_REQUEST, "이미 반납된 도서입니다.", LogLevel.WARN),

    RESERVATION_NOT_FOUND(ErrorCode.NOT_FOUND, "예약을 찾을 수 없습니다.", LogLevel.WARN),
    BOOK_AVAILABLE_NO_RESERVATION(ErrorCode.BAD_REQUEST, "대출 가능한 도서는 예약할 수 없습니다.", LogLevel.WARN),
    RESERVATION_LIMIT_EXCEEDED(ErrorCode.CONFLICT, "해당 도서의 예약 한도를 초과했습니다.", LogLevel.WARN),
    RESERVATION_ALREADY_EXISTS(ErrorCode.CONFLICT, "이미 해당 도서에 예약이 존재합니다.", LogLevel.WARN),
    USER_RESERVATION_LIMIT_EXCEEDED(ErrorCode.CONFLICT, "사용자 예약 한도(2권)를 초과했습니다.", LogLevel.WARN),
    RESERVATION_NOT_OWNER(ErrorCode.FORBIDDEN, "본인의 예약만 취소할 수 있습니다.", LogLevel.WARN),
    RESERVATION_ALREADY_CANCELLED(ErrorCode.BAD_REQUEST, "이미 취소된 예약입니다.", LogLevel.WARN),

    BOOK_REQUEST_ALREADY_EXISTS(ErrorCode.CONFLICT, "이미 보유 중인 도서입니다.", LogLevel.WARN),
    BOOK_REQUEST_DUPLICATE(ErrorCode.CONFLICT, "동일한 ISBN으로 대기 중인 신청이 존재합니다.", LogLevel.WARN),
    BOOK_REQUEST_STATUS_ALREADY_PROCESSED(ErrorCode.BAD_REQUEST, "이미 처리된 신청입니다.", LogLevel.WARN),
    BOOK_REQUEST_NOT_OWNER(ErrorCode.FORBIDDEN, "본인의 신청만 취소할 수 있습니다.", LogLevel.WARN),

    NOTIFICATION_NOT_FOUND(ErrorCode.NOT_FOUND, "알림을 찾을 수 없습니다.", LogLevel.WARN),
    NOTIFICATION_NOT_OWNER(ErrorCode.FORBIDDEN, "본인의 알림만 처리할 수 있습니다.", LogLevel.WARN);


    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;
}
