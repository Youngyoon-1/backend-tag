package com.tag.dto.response;

import lombok.Getter;

@Getter
public class ExceptionResponse {

    private String message;

    private ExceptionResponse() {
    }

    public ExceptionResponse(final String message) {
        this.message = message;
    }
}
