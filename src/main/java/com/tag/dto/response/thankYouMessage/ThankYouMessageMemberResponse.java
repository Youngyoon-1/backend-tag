package com.tag.dto.response.thankYouMessage;

import com.tag.domain.member.Member;
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
