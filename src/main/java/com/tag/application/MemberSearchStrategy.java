package com.tag.application;

import com.tag.domain.Member;
import com.tag.dto.response.MemberResponse;
import java.util.List;

public abstract class MemberSearchStrategy {

    abstract boolean isApplicable(final List<String> memberSearchCategory);

    abstract void populateMemberResponse(final Member member, final MemberResponse memberResponse);

    public void populateMemberResponse(final List<String> memberSearchCategory, final Member member,
                                       final MemberResponse memberResponse) {
        if (isApplicable(memberSearchCategory)) {
            populateMemberResponse(member, memberResponse);
        }
    }
}
