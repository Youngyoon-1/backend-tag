package com.tag.dto.response;

import lombok.Getter;

@Getter
public class CommentCountResponse {

    private long count;

    public CommentCountResponse(final long count) {
        this.count = count;
    }

    private CommentCountResponse() {
    }
}
