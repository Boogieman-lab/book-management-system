package com.example.bookmanagementsystembo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "404: 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500: 서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "400: 클라이언트 요청 오류입니다."),
    CONFLICT(HttpStatus.CONFLICT,"409: 요청 충돌 발생했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "401: 인증에 실패했습니다.")
    ;
    private final HttpStatus status;
    private final String message;
}
