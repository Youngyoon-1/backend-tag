package com.tag.application;

import com.tag.domain.Member;
import com.tag.dto.request.MemberSearchCategory;
import com.tag.dto.response.MemberResponse;
import org.springframework.stereotype.Component;

@Component
public final class QrImageUrlSearchStrategy extends MemberSearchStrategy {

    private final ObjectStorageManager objectStorageManager;

    public QrImageUrlSearchStrategy(final ObjectStorageManager objectStorageManager) {
        this.objectStorageManager = objectStorageManager;
    }

    @Override
    boolean isApplicable(final String memberSearchCategory) {
        return MemberSearchCategory.hasQrImageUrlFromParam(memberSearchCategory);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String qrImageName = member.getQrImageName();
        final String qrImageUrl = objectStorageManager.createGetUrl(qrImageName, MemberImageCategory.QR);
        memberResponse.setQrImageUrl(qrImageUrl);
    }
}
