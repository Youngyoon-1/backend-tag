package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ThankYouMessageMemberResponse {

    private long id;
    private String email;
    private String profileUrl;

    private ThankYouMessageMemberResponse() {
    }

    public ThankYouMessageMemberResponse(final Member member, final String profileUrl) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.profileUrl = profileUrl;
    }
}
