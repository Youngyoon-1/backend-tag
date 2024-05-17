package com.tag.dto.response.comment;

import com.tag.domain.comment.Comment;

public record CommentResponse(long id, CommentMemberResponse memberResponse, String content) {

    public static CommentResponse of(final Comment comment, final String profileUrl) {
        return new CommentResponse(
                comment.getId(),
                new CommentMemberResponse(comment.getMember(), profileUrl),
                comment.getContent()
        );
    }
}
