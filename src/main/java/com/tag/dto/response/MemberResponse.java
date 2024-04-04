package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberResponse {

    private String email;
    private String introductoryArticle;
    private String profileImageUrl;
    private String profileImageName;
    private String qrImageUrl;
    private String qrLinkUrl;

    public MemberResponse() {
    }

    public MemberResponse(final String email, final String introductoryArticle, final String profileImageUrl,
                          final String profileImageName,
                          final String qrImageUrl,
                          final String qrLinkUrl) {
        this.email = email;
        this.introductoryArticle = introductoryArticle;
        this.profileImageUrl = profileImageUrl;
        this.profileImageName = profileImageName;
        this.qrImageUrl = qrImageUrl;
        this.qrLinkUrl = qrLinkUrl;
    }

    public MemberResponse(final Member member, final String profileImageUrl) {
        this.email = member.getEmail();
        this.introductoryArticle = member.getIntroductoryArticle();
        this.profileImageUrl = profileImageUrl;
        this.profileImageName = member.getProfileImageName();
    }
}
