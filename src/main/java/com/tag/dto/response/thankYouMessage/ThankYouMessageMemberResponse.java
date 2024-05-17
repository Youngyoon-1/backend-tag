package com.tag.dto.response.thankYouMessage;

import com.tag.domain.member.Member;

public record ThankYouMessageMemberResponse(long id, String email, String profileUrl) {
    public ThankYouMessageMemberResponse(final Member member, final String profileUrl) {
        this(member.getId(), member.getEmail(), profileUrl);
    }
}
