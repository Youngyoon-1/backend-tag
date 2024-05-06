package com.tag.presentation;

import com.tag.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeException(final RuntimeException e) {
        log.warn("RuntimeException, Message : {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        log.error("Exception", e);
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("서버 에러가 발생했습니다."));
    }
}
