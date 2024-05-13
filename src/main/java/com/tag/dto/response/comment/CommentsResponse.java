package com.tag.dto.response.comment;

import com.tag.dto.response.comment.CommentResponse;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CommentsResponse {

    private Long cursor;
    private List<CommentResponse> commentResponses;

    private CommentsResponse() {
    }

    public CommentsResponse(final Long cursor, final List<CommentResponse> commentResponses) {
        this.cursor = cursor;
        this.commentResponses = commentResponses;
    }
}
