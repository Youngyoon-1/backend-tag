package com.tag.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CommentRequest {

    @Size(min = 1, max = 400)
    private String content;

    private CommentRequest() {
    }

    public CommentRequest(final String content) {
        this.content = content;
    }
}
