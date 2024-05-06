package com.tag.application;

import com.tag.domain.Member;
import com.tag.dto.request.MemberSearchCategory;
import com.tag.dto.response.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class QrLinkUrlSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategories) {
        return MemberSearchCategory.hasQrLinkUrlFromParam(memberSearchCategories);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String qrLinkUrl = member.getQrLinkUrl();
        memberResponse.setQrLinkUrl(qrLinkUrl);
    }
}
