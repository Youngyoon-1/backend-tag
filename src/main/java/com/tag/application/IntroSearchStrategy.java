package com.tag.application;

import com.tag.domain.Member;
import com.tag.dto.request.MemberSearchCategory;
import com.tag.dto.response.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class IntroSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategories) {
        re
        return MemberSearchCategory.hasIntroductionFromParam(memberSearchCategories);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String introduction = member.getIntroduction();
        memberResponse.setIntroductoryArticle(introduction);
    }
}
