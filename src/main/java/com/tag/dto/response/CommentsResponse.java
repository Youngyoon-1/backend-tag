package com.tag.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class CommentsResponse {

    private Long cursor;
    private List<CommentResponse> commentResponses;

    private CommentsResponse() {
    }

    public CommentsResponse(final Long cursor, final List<CommentResponse> commentResponses) {
        this.cursor = cursor;
        this.commentResponses = commentResponses;
    }

//    public static CommentsResponse from(final List<Comment> comments) {
//        final List<CommentResponse> commentResponses = comments.stream()
//                .map(CommentResponse::new)
//                .collect(Collectors.toList());
//        return new CommentsResponse(commentResponses);
//    }
}
