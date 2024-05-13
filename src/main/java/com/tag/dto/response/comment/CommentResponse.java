package com.tag.dto.response.comment;

import com.tag.domain.comment.Comment;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class CommentResponse {

    private long id;
    private CommentMemberResponse memberResponse;
    private String content;

    public CommentResponse(final long id, final CommentMemberResponse memberResponse, final String content) {
        this.id = id;
        this.memberResponse = memberResponse;
        this.content = content;
    }

    private CommentResponse() {
    }

    public static CommentResponse of(final Comment comment, final String profileUrl) {
        return new CommentResponse(
                comment.getId(),
                new CommentMemberResponse(comment.getMember(), profileUrl),
                comment.getContent()
        );
    }
}
