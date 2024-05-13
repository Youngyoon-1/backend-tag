package com.tag.application.member.searchStrategy;

import com.tag.domain.member.Member;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;

public abstract class MemberSearchStrategy {

    abstract boolean isApplicable(final List<String> memberSearchCategories);

    abstract void populateMemberResponse(final Member member, final MemberResponse memberResponse);

    public void populateMemberResponse(final List<String> memberSearchCategories, final Member member,
                                       final MemberResponse memberResponse) {
        if (isApplicable(memberSearchCategories)) {
            populateMemberResponse(member, memberResponse);
        }
    }
}
