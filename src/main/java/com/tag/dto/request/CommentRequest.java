package com.tag.dto.request;

import lombok.Getter;

@Getter
public class CommentRequest {

    private String content;

    private CommentRequest() {
    }

    public CommentRequest(final String content) {
        this.content = content;
    }
}
