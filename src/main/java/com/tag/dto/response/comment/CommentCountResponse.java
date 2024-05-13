package com.tag.dto.response.comment;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CommentCountResponse {

    private long count;

    public CommentCountResponse(final long count) {
        this.count = count;
    }

    private CommentCountResponse() {
    }
}
