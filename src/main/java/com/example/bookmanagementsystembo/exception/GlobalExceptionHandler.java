package com.example.bookmanagementsystembo.exception;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
//@Hidden
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * CoreException을 처리하는 메서드
     */
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException coreException) {
        logError(coreException.getErrorType());
        ErrorResponse errorResponse = ErrorResponse.of(
                coreException.getErrorType().getCode().getMessage(),
                coreException.getErrorType().getMessage(),
                coreException.getPayload()
        );
        return ResponseEntity.status(coreException.getErrorType().getCode().getStatus()).body(errorResponse);
    }

    /**
     * 요청 데이터 검증 실패시 처리하는 메서드
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        logError(ErrorType.FIELD_ERROR_DEFAULT);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = (fieldError != null) ? fieldError.getDefaultMessage() : ErrorType.FIELD_ERROR_DEFAULT.getMessage();

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST.getMessage(), errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 그 외의 모든 예외를 처리하는 메서드
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception) {
        logError(ErrorType.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), exception.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(errorResponse);
    }


    private void logError(ErrorType errorType) {
        LogLevel logLevel = errorType.getLogLevel();
        switch (logLevel) {
            case WARN -> log.warn(errorType.getMessage());
            case ERROR -> log.error(errorType.getMessage());
            default -> log.info(errorType.getMessage());
        }
    }
}
