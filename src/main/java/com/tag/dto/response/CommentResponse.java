package com.tag.dto.response;

import com.tag.domain.Comment;
import lombok.Getter;

@Getter
public class CommentResponse {

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

    //    public CommentResponse(final Comment comment) {
//        this.id = comment.getId();
//        this.memberId = comment.getMemberId();
//        this.content = comment.getContent();
//    }
    public static CommentResponse from(final Comment comment, final String profileUrl) {
        return new CommentResponse(
                comment.getId(),
                new CommentMemberResponse(comment.getMember(), profileUrl),
                comment.getContent()
        );
    }
}
