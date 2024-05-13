package com.tag.application.member.searchStrategy;

import com.tag.domain.member.Member;
import com.tag.dto.request.member.MemberSearchCategory;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class EmailSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategory) {
        return memberSearchCategory.stream()
                .anyMatch(MemberSearchCategory.VIEW_EMAIL.value::equalsIgnoreCase);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String email = member.getEmail();
        memberResponse.setEmail(email);
    }
}
