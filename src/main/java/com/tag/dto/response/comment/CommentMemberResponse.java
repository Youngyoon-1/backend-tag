package com.tag.dto.response.comment;

import com.tag.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public final class CommentMemberResponse {

    private long id;
    private String email;
    private String profileUrl;

    public CommentMemberResponse(final Member member, final String profileUrl) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.profileUrl = profileUrl;
    }
}
