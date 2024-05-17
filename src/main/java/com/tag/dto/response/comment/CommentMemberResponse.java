package com.tag.dto.response.comment;

import com.tag.domain.member.Member;

public record CommentMemberResponse(long id, String email, String profileUrl) {

    public CommentMemberResponse(final Member member, final String profileUrl) {
        this(member.getId(), member.getEmail(), profileUrl);
    }
}
