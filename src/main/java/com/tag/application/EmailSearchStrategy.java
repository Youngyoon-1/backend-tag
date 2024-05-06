package com.tag.application;

import com.tag.domain.Member;
import com.tag.dto.request.MemberSearchCategory;
import com.tag.dto.response.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class EmailSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategory) {
        return memberSearchCategory.stream()
                .anyMatch(MemberSearchCategory::hasEmailFromParam);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String email = member.getEmail();
        memberResponse.setEmail(email);
    }
}
