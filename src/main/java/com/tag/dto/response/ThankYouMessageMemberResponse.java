package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.Getter;

@Getter
public class ThankYouMessageMemberResponse {

    private long id;
    private String email;
    private String profileUrl;

    public ThankYouMessageMemberResponse(final Member member, final String profileUrl) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.profileUrl = profileUrl;
    }
}
