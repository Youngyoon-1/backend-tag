package com.tag.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.application.AccessTokenProvider;
import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.dto.request.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.MemberImageNameUpdateRequest;
import com.tag.dto.request.MemberInfoUpdateRequest;
import com.tag.dto.request.MemberProfileUpdateRequest;
import com.tag.dto.response.ExceptionResponse;
import com.tag.dto.response.LoginResponse;
import com.tag.dto.response.MemberDonationInfoResponse;
import com.tag.dto.response.MemberImageUploadUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
import com.tag.dto.response.MemberResponse;
import java.net.URI;
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
    void 최초_가입자인_회원_정보와_이미지를_조회한다_() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        final Member savedMember = memberRepository.save(member);

        // when
        final Long id = savedMember.getId();
        final ResponseEntity<MemberResponse> memberResponseEntity = testRestTemplate.getForEntity(
                "/api/members/" + id,
                MemberResponse.class
        );

        // then
        final HttpStatusCode statusCode = memberResponseEntity.getStatusCode();
        final MemberResponse memberResponse = memberResponseEntity.getBody();
        final String email = memberResponse.getEmail();
        final String introductoryArticle = memberResponse.getIntroductoryArticle();
        final String profilePhotoUrl = memberResponse.getProfileImageUrl();
        final String qrPhotoUrl = memberResponse.getQrImageUrl();
        final String qrLinkUrl = memberResponse.getQrLinkUrl();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(email).isNotNull(),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(profilePhotoUrl).isNull(),
                () -> assertThat(qrPhotoUrl).isNull(),
                () -> assertThat(qrLinkUrl).isNull()
        );
    }

    @Test
    void 소개글_프로필_이미지_큐알_이미지_큐알_링크를_등록한_회원_정보와_이미지를_조회한다_() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .introduction("introduction")
                .profileImageName("profileImageName")
                .qrImageName("qrImageName")
                .qrLinkUrl("qrLinkUrl")
                .build();
        final Member savedMember = memberRepository.save(member);

        // when
        final Long id = savedMember.getId();
        final ResponseEntity<MemberResponse> memberResponseEntity = testRestTemplate.getForEntity(
                "/api/members/" + id,
                MemberResponse.class
        );

        // then
        final HttpStatusCode statusCode = memberResponseEntity.getStatusCode();
        final MemberResponse memberResponse = memberResponseEntity.getBody();
        final String email = memberResponse.getEmail();
        final String introductoryArticle = memberResponse.getIntroductoryArticle();
        final String qrLinkUrl = memberResponse.getQrLinkUrl();
        final String profileImageUrl = memberResponse.getProfileImageUrl();
        final HttpStatusCode profileImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(profileImageUrl),
                        Void.class)
                .getStatusCode();
        final String qrImageUrl = memberResponse.getQrImageUrl();
        final HttpStatusCode qrImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(qrImageUrl),
                        Void.class)
                .getStatusCode();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isEqualTo("introduction"),
                () -> assertThat(qrLinkUrl).isEqualTo("qrLinkUrl"),
                () -> assertThat(profileImageResponseStatusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(qrImageResponseStatusCode).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    void 프로필_이미지를_등록한_회원의_이메일과_프로필_이미지_url_을_조회한다_() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .profileImageName("profileImageName")
                .build();
        final Member savedMember = memberRepository.save(member);

        // when
        final Long id = savedMember.getId();
        final ResponseEntity<MemberResponse> memberResponseEntity = testRestTemplate.getForEntity(
                "/api/members/" + id + "?searchCategory=email,profileImageUrl",
                MemberResponse.class
        );

        // then
        final HttpStatusCode statusCode = memberResponseEntity.getStatusCode();
        final MemberResponse memberResponse = memberResponseEntity.getBody();
        final String email = memberResponse.getEmail();
        final String introductoryArticle = memberResponse.getIntroductoryArticle();
        final String qrLinkUrl = memberResponse.getQrLinkUrl();
        final String profileImageUrl = memberResponse.getProfileImageUrl();
        final HttpStatusCode profileImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(profileImageUrl),
                        Void.class)
                .getStatusCode();
        final String qrImageUrl = memberResponse.getQrImageUrl();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> assertThat(profileImageResponseStatusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(qrImageUrl).isNull()
        );
    }

    @Test
    void 회원_정보를_조회한다_존재하지_않는_회원일_경우_400_응답_코드가_반환된다() {
        // when
        final ResponseEntity<ExceptionResponse> response = testRestTemplate.getForEntity(
                "/api/members/10000",
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = response.getStatusCode();
        final String message = response.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(message).isEqualTo("존재하지 않는 아이디 입니다.")
        );
    }

    @Test
    void 로그인을_하고_프로필_사진을_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.getAccessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "profileImageName");
        final HttpEntity httpEntity = new HttpEntity(memberImageNameUpdateRequest, httpHeaders);
        final ResponseEntity<MemberImageUploadUrlResponse> imageUrlResponseEntity = testRestTemplate.exchange(
                "/api/members/me/image-name?imageCategory=profile",
                HttpMethod.PATCH,
                httpEntity,
                MemberImageUploadUrlResponse.class
        );

        // then
        final HttpStatusCode statusCode = imageUrlResponseEntity.getStatusCode();
        final String profileImageUrl = imageUrlResponseEntity.getBody()
                .getUrl();
        final HttpStatusCode profileImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(profileImageUrl),
                        Void.class)
                .getStatusCode();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(profileImageResponseStatusCode).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    void 로그인을_하고_큐알_이미지를_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.getAccessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "qrImageName");
        final HttpEntity httpEntity = new HttpEntity(memberImageNameUpdateRequest, httpHeaders);
        final ResponseEntity<MemberImageUploadUrlResponse> imageUrlResponseEntity = testRestTemplate.exchange(
                "/api/members/me/image-name?imageCategory=qr",
                HttpMethod.PATCH,
                httpEntity,
                MemberImageUploadUrlResponse.class
        );

        // then
        final HttpStatusCode statusCode = imageUrlResponseEntity.getStatusCode();
        final String qrImageUrl = imageUrlResponseEntity.getBody()
                .getUrl();
        final HttpStatusCode qrImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(qrImageUrl),
                        Void.class)
                .getStatusCode();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(qrImageResponseStatusCode).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    void 프로필_사진을_수정한다_로그인_상태가_아니면_400_응답값이_반환된다() {
        // when
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "profileImageName");
        final HttpEntity httpEntity = new HttpEntity(memberImageNameUpdateRequest);
        final ResponseEntity<ExceptionResponse> exceptionResponseEntity = testRestTemplate.exchange(
                "/api/members/me/image-name?memberImageCategory=profile",
                HttpMethod.PATCH,
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = exceptionResponseEntity.getStatusCode();
        final String exceptionMessage = exceptionResponseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(exceptionMessage).isEqualTo("토큰이 존재하지 않습니다.")
        );
    }

    @Test
    void 큐알_이미지를_수정한다_위조된_엑세스_토큰으로_요청하면_400_응답값이_반환된다() {
        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth("malformedAccessToken");
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "qrImageName");
        final HttpEntity httpEntity = new HttpEntity(memberImageNameUpdateRequest, httpHeaders);
        final ResponseEntity<ExceptionResponse> exceptionResponseEntity = testRestTemplate.exchange(
                "/api/members/me/image-name?memberImageCategory=qr",
                HttpMethod.PATCH,
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = exceptionResponseEntity.getStatusCode();
        final String exceptionMessage = exceptionResponseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(exceptionMessage).isEqualTo("토큰 값이 유효하지 않아 추출할 수 없습니다.")
        );
    }

    @Test
    void 큐알_이미지를_수정한다_만료된_엑세스_토큰으로_요청하면_400_응답값이_반환된다() {
        // when
        final AccessTokenProvider expiredAccessTokenProvider = new AccessTokenProvider(
                "secretKeysecretKeysecretKeysecretKeysecretKeysecretKey", 0);
        final String expiredAccessToken = expiredAccessTokenProvider.issueToken(10L);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(expiredAccessToken);
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "qrImageName");
        final HttpEntity httpEntity = new HttpEntity(memberImageNameUpdateRequest, httpHeaders);
        final ResponseEntity<ExceptionResponse> exceptionResponseEntity = testRestTemplate.exchange(
                "/api/members/me/image-name?memberImageCategory=qr",
                HttpMethod.PATCH,
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = exceptionResponseEntity.getStatusCode();
        final String exceptionMessage = exceptionResponseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(exceptionMessage).isEqualTo("토큰이 만료되었습니다.")
        );
    }

    @Test
    void 로그인을_하고_소개글을_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.getAccessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final MemberInfoUpdateRequest memberInfoUpdateRequest = new MemberInfoUpdateRequest(
                "introductoryArticleContent");
        final HttpEntity httpEntity = new HttpEntity(memberInfoUpdateRequest, httpHeaders);
        final ResponseEntity<MemberInfoUpdateResponse> memberInfoUpdateResponseEntity = testRestTemplate.exchange(
                "/api/members/me/info?infoCategory=introductoryArticle",
                HttpMethod.PATCH,
                httpEntity,
                MemberInfoUpdateResponse.class
        );

        // then
        final HttpStatusCode statusCode = memberInfoUpdateResponseEntity.getStatusCode();
        final String introductoryArticle = memberInfoUpdateResponseEntity.getBody()
                .getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(introductoryArticle).isEqualTo("introductoryArticleContent")
        );
    }

    @Test
    void 로그인을_하고_큐알_링크를_수정한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.getAccessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final MemberInfoUpdateRequest memberInfoUpdateRequest = new MemberInfoUpdateRequest(
                "https://qr.kokoapay.com/Ej7rDe0");
        final HttpEntity httpEntity = new HttpEntity(memberInfoUpdateRequest, httpHeaders);
        final ResponseEntity<MemberInfoUpdateResponse> memberInfoUpdateResponseEntity = testRestTemplate.exchange(
                "/api/members/me/info?infoCategory=qrLinkUrl",
                HttpMethod.PATCH,
                httpEntity,
                MemberInfoUpdateResponse.class
        );

        // then
        final HttpStatusCode statusCode = memberInfoUpdateResponseEntity.getStatusCode();
        final String introductoryArticle = memberInfoUpdateResponseEntity.getBody()
                .getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(introductoryArticle).isEqualTo("https://qr.kokoapay.com/Ej7rDe0")
        );
    }

    @Test
    void 회원의_후원_정보를_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .bankName("bankName")
                .accountNumber("123456789")
                .accountHolder("accountHolder")
                .remitLink("remitLink")
                .build();
        memberRepository.save(member);

        // when
        final ResponseEntity<MemberDonationInfoResponse> responseEnity = testRestTemplate.getForEntity(
                "/api/members/" + member.getId() + "/donation-info",
                MemberDonationInfoResponse.class
        );

        // then
        final HttpStatusCode statusCode = responseEnity.getStatusCode();
        final MemberDonationInfoResponse memberInfoUpdateResponse = responseEnity.getBody();
        final String bankName = memberInfoUpdateResponse.getBankName();
        final String accountNumber = memberInfoUpdateResponse.getAccountNumber();
        final String accountHolder = memberInfoUpdateResponse.getAccountHolder();
        final String remitLink = memberInfoUpdateResponse.getRemitLink();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(bankName).isEqualTo("bankName"),
                () -> assertThat(accountNumber).isEqualTo("123456789"),
                () -> assertThat(accountHolder).isEqualTo("accountHolder"),
                () -> assertThat(remitLink).isEqualTo("remitLink")
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
        final String accessToken = loginResponse.getAccessToken();
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
        final String accessToken = loginResponse.getAccessToken();
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
        final String accessToken = loginResponse.getAccessToken();
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
        final String accessToken = loginResponse.getAccessToken();
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
    void 회원의_프로필_이미지를_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .profileImageName("profileImageName")
                .build();
        memberRepository.save(member);

        // when
        final ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                "/api/members/" + member.getId() + "/profile-image",
                String.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final String profileImageUrl = responseEntity.getBody();
        final HttpStatusCode profileImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(profileImageUrl),
                        Void.class)
                .getStatusCode();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(profileImageResponseStatusCode).isEqualTo(HttpStatus.OK)
        );
    }

    @Test
    void 이메일_수신을_거부한_회원의_이메일_수신_동의_여부를_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        memberRepository.save(member);

        // when
        final ResponseEntity<Boolean> responseEntity = testRestTemplate.getForEntity(
                "/api/members/" + member.getId() + "/mail-notification",
                Boolean.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final Boolean isConfirmed = responseEntity.getBody();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(isConfirmed).isFalse()
        );
    }

    @Test
    void 이메일_수신을_동의한_회원의_이메일_수신_동의_여부를_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .isConfirmedMailNotification(true)
                .build();
        memberRepository.save(member);

        // when
        final ResponseEntity<Boolean> responseEntity = testRestTemplate.getForEntity(
                "/api/members/" + member.getId() + "/mail-notification",
                Boolean.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final Boolean isConfirmed = responseEntity.getBody();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(isConfirmed).isTrue()
        );
    }

    @Test
    void 로그인을_하고_이메일_수신을_동의한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.getAccessToken();
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
        final String accessToken = loginResponse.getAccessToken();
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
