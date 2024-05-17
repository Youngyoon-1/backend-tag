package com.tag.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.dto.request.member.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.member.MemberProfileUpdateRequest;
import com.tag.dto.response.auth.LoginResponse;
import com.tag.dto.response.exception.ExceptionResponse;
import com.tag.dto.response.member.MemberResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@AcceptanceTest
public class MemberAcceptanceTest extends WithTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원정보를_전체_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .introduction("자기소개입니다.")
                .profileImageName("프로필이미지이름")
                .isConfirmedMailNotification(true)
                .bankName("한국은행")
                .accountNumber("123456789")
                .accountHolder("테스트")
                .remitLink("https://remit.kokoapay.com/1231231231")
                .build();
        final Member savedMember = memberRepository.save(member);

        // when
        final long id = savedMember.getId();
        final ResponseEntity<MemberResponse> memberResponseEntity = testRestTemplate.getForEntity(
                "/api/members/" + id
                        + "?searchCategories=email,introduction,profileImageUrl,profileImageName,mailNotification,donationInfo",
                MemberResponse.class
        );

        // then
        final HttpStatusCode statusCode = memberResponseEntity.getStatusCode();
        final MemberResponse memberResponse = memberResponseEntity.getBody();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(memberResponse).usingRecursiveComparison()
                        .ignoringFields("profileImageUrl")
                        .isEqualTo(member)
        );
    }

    @Test
    void 회원_정보를_조회한다_존재하지_않는_회원일_경우_400_응답_코드가_반환된다() {
        // when
        final ResponseEntity<ExceptionResponse> response = testRestTemplate.getForEntity(
                "/api/members/10000?searchCategories=",
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = response.getStatusCode();
        final String message = response.getBody()
                .message();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(message).contains("존재하지 않는 회원")
        );
    }

    @Test
    void 로그인을_하고_자신의_후원정보를_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest = new MemberDonationInfoUpdateRequest(
                "bankName",
                "123456789",
                "accountHolder",
                "remitLink123"
        );
        final String accessToken = loginResponse.accessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity(memberDonationInfoUpdateRequest, httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/members/me/donation-info",
                HttpMethod.PATCH,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_자신의_회원_정보를_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest(
                "introduction",
                "profileImageName"
        );
        final String accessToken = loginResponse.accessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity(memberProfileUpdateRequest, httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/members/me/profile",
                HttpMethod.PATCH,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_이용약관_및_개인정보처리방침에_동의한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.accessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity(httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/members/me",
                HttpMethod.PATCH,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_이메일_수신_동의를_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.accessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity(httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/members/me/mail-notification?isConfirmed=true",
                HttpMethod.PATCH,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_이메일_수신을_동의한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.accessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity(httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/members/me/mail-notification?isConfirmed=true",
                HttpMethod.PATCH,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_이메일_수신을_거절한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.accessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity(httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/members/me/mail-notification?isConfirmed=false",
                HttpMethod.PATCH,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
