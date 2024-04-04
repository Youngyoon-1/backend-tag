package com.tag.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginResponseTest {

    @Test
    void loginReulst_로_로그인_응답을_생성한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .introductoryArticle("introductoryArticle")
                .profileImageName("profileImageName")
                .qrImageName("qrImageName")
                .qrLinkUrl("qrLinkUrl")
                .build();
        final LoginResult loginResult = new LoginResult(member, "profileImageUrl", "qrImageUrl", "accessToken",
                "refreshToken");

        // when
        final LoginResponse loginResponse = new LoginResponse(loginResult);

        // then
        final String email = loginResponse.getEmail();
        final String introductoryArticle = loginResponse.getIntroductoryArticle();
        final String profileImageUrl = loginResponse.getProfileImageUrl();
        final String qrImageUrl = loginResponse.getQrImageUrl();
        final String qrLinkUrl = loginResponse.getQrLinkUrl();
        final String accessToken = loginResponse.getAccessToken();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isEqualTo("introductoryArticle"),
                () -> assertThat(profileImageUrl).isEqualTo("profileImageUrl"),
                () -> assertThat(qrImageUrl).isEqualTo("qrImageUrl"),
                () -> assertThat(qrLinkUrl).isEqualTo("qrLinkUrl"),
                () -> assertThat(accessToken).isEqualTo("accessToken")
        );
    }
}
