package com.tag.application.member.searchStrategy;

import com.tag.domain.member.Member;
import com.tag.dto.request.member.MemberSearchCategory;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class MailNotificationSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategories) {
        return memberSearchCategories.stream()
                .anyMatch(MemberSearchCategory.VIEW_MAIL_NOTIFICATION.value::equalsIgnoreCase);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final boolean isConfirmedMailNotification = member.isConfirmedMailNotification();
        memberResponse.setIsConfirmedMailNotification(isConfirmedMailNotification);
    }
}
