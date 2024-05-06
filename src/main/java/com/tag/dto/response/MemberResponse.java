package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public final class MemberResponse {

    private String email;
    private String introductoryArticle;
    private String profileImageUrl;
    private String profileImageName;
    private String qrImageUrl;
    private String qrLinkUrl;

    public MemberResponse() {
    }

    public MemberResponse(final Member member, final String profileImageUrl, final String qrImageUrl) {
        this.email = member.getEmail();
        this.introductoryArticle = member.getIntroduction();
        this.profileImageUrl = profileImageUrl;
        this.profileImageName = member.getProfileImageName();
        this.qrLinkUrl = member.getQrLinkUrl();
        this.qrImageUrl = qrImageUrl;
    }
}
