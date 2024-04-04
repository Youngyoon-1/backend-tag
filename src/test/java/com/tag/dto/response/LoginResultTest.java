package com.tag.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginResultTest {

    @Test
    void 로그인_응답을_생성한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .introductoryArticle("introductoryArticle")
                .profileImageName("profileImageName")
                .qrImageName("qrImageName")
                .qrLinkUrl("qrLink")
                .build();

        // when
        final LoginResult loginResult = new LoginResult(member, "profileImageUrl", "qrImageUrl", "accessToken",
                "refreshToken");

        // then
        final String email = loginResult.getEmail();
        final String introductoryArticle = loginResult.getIntroductoryArticle();
        final String profileImageUrl = loginResult.getProfileImageUrl();
        final String qrImageUrl = loginResult.getQrImageUrl();
        final String qrLinkUrl = loginResult.getQrLinkUrl();
        final String accessToken = loginResult.getAccessToken();
        final String refreshToken = loginResult.getRefreshToken();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isEqualTo("introductoryArticle"),
                () -> assertThat(profileImageUrl).isEqualTo("profileImageUrl"),
                () -> assertThat(qrImageUrl).isEqualTo("qrImageUrl"),
                () -> assertThat(qrLinkUrl).isEqualTo("qrLink"),
                () -> assertThat(accessToken).isEqualTo("accessToken"),
                () -> assertThat(refreshToken).isEqualTo("refreshToken")
        );
    }
}
