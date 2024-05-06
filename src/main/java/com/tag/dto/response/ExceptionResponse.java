package com.tag.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExceptionResponse {

    private String message;

    private ExceptionResponse() {
    }

    public ExceptionResponse(final String message) {
        this.message = message;
    }
}
