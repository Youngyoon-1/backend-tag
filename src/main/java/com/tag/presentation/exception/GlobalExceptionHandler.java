package com.tag.presentation.exception;

import com.tag.dto.response.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler {

    private static final String PREFIX_MESSAGE_METHOD_ARGUMENT_NOT_VALID_EXCEPTION = "MethodArgumentNotValidException, Message : {}";
    private static final String PREFIX_MESSAGE_RUNTIME_EXCEPTION = "RuntimeException, Message : {}";
    private static final String PREFIX_MESSAGE_EXCEPTION = "Exception : ";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "서버 에러가 발생했습니다.";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        final String message = e.getMessage();
        log.warn(PREFIX_MESSAGE_METHOD_ARGUMENT_NOT_VALID_EXCEPTION, message);
        final ExceptionResponse exceptionResponse = new ExceptionResponse(message);
        return ResponseEntity.badRequest()
                .body(exceptionResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(final RuntimeException e) {
        final String message = e.getMessage();
        log.warn(PREFIX_MESSAGE_RUNTIME_EXCEPTION, message);
        final ExceptionResponse exceptionResponse = new ExceptionResponse(message);
        return ResponseEntity.badRequest()
                .body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        log.error(PREFIX_MESSAGE_EXCEPTION, e);
        final ExceptionResponse exceptionResponse = new ExceptionResponse(INTERNAL_SERVER_ERROR_MESSAGE);
        return ResponseEntity.internalServerError()
                .body(exceptionResponse);
    }
}
