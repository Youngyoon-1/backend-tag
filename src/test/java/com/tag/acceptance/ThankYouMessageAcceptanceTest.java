package com.tag.acceptance;

import static com.tag.application.auth.AccessTokenProvider.TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import com.tag.application.auth.AccessTokenProvider;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.domain.thankYouMessage.ThankYouMessage;
import com.tag.domain.thankYouMessage.ThankYouMessageRepository;
import com.tag.dto.request.thankYouMessage.ThankYouMessageRequest;
import com.tag.dto.response.exception.ExceptionResponse;
import com.tag.dto.response.auth.LoginResponse;
import com.tag.dto.response.thankYouMessage.ThankYouMessageResponse;
import com.tag.dto.response.thankYouMessage.ThankYouMessagesResponse;
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
public class ThankYouMessageAcceptanceTest extends WithTestcontainers {

    @Autowired
    private ThankYouMessageRepository thankYouMessageRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 감사_메세지_2_개를_조회한다() {
        // given
        final Member member1 = createMember("test1");
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member1)
                .content("thankYouMessageContent1")
                .build();
        final Long thankYouMessage1Id = thankYouMessageRepository.save(thankYouMessage1)
                .getId();
        final Member member2 = createMember("test2");
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member2)
                .content("thankYouMessageContent2")
                .build();
        final Long thankYouMessage2Id = thankYouMessageRepository.save(thankYouMessage2)
                .getId();

        // when
        final ResponseEntity<ThankYouMessagesResponse> thankYouMessagesResponseEntity = testRestTemplate.getForEntity(
                "/api/members/10/thankYouMessages?pageSize=2",
                ThankYouMessagesResponse.class
        );

        // then
        final HttpStatusCode statusCode = thankYouMessagesResponseEntity.getStatusCode();
        final ThankYouMessageResponse thankYouMessage1Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(0);
        final long actualThankYouMessage1Id = thankYouMessage1Response.getId();
        final long thankYouMessage1WriterMemberId = thankYouMessage1Response.getMemberResponse()
                .getId();
        final String thankYouMessage1Content = thankYouMessage1Response.getContent();
        final ThankYouMessageResponse thankYouMessage2Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(1);
        final long actualThankYouMessage2Id = thankYouMessage2Response.getId();
        final long thankYouMessage2WriterMemberId = thankYouMessage2Response.getMemberResponse()
                .getId();
        final String thankYouMessage2Content = thankYouMessage2Response.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(actualThankYouMessage1Id).isEqualTo(thankYouMessage2Id),
                () -> assertThat(thankYouMessage1WriterMemberId).isEqualTo(member2.getId()),
                () -> assertThat(thankYouMessage1Content).isEqualTo("thankYouMessageContent2"),
                () -> assertThat(actualThankYouMessage2Id).isEqualTo(thankYouMessage1Id),
                () -> assertThat(thankYouMessage2WriterMemberId).isEqualTo(member1.getId()),
                () -> assertThat(thankYouMessage2Content).isEqualTo("thankYouMessageContent1")
        );
    }

    private Member createMember(final String email) {
        return memberRepository.save(Member.builder().email(email).build());
    }

    @Test
    void 감사_메세지_2_개를_조회한다_조회가_시작될_아이디가_주어진_경우() {
        // given
        final Member member1 = createMember("test1");
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member1)
                .content("thankYouMessageContent1")
                .build();
        final Long thankYouMessage1Id = thankYouMessageRepository.save(thankYouMessage1)
                .getId();
        final Member member2 = createMember("test2");
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member2)
                .content("thankYouMessageContent2")
                .build();
        final Long thankYouMessage2Id = thankYouMessageRepository.save(thankYouMessage2)
                .getId();
        final Member member3 = createMember("test3");
        final ThankYouMessage thankYouMessage3 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member3)
                .content("thankYouMessageContent3")
                .build();
        thankYouMessageRepository.save(thankYouMessage3);

        // when
        final ResponseEntity<ThankYouMessagesResponse> thankYouMessagesResponseEntity = testRestTemplate.getForEntity(
                "/api/members/10/thankYouMessages?pageSize=2&cursor=" + thankYouMessage3.getId(),
                ThankYouMessagesResponse.class
        );

        // then
        final HttpStatusCode statusCode = thankYouMessagesResponseEntity.getStatusCode();
        final ThankYouMessageResponse thankYouMessage1Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(0);
        final long actualThankYouMessage1Id = thankYouMessage1Response.getId();
        final long thankYouMessage1MemberId = thankYouMessage1Response.getMemberResponse()
                .getId();
        final String thankYouMessage1Content = thankYouMessage1Response.getContent();
        final ThankYouMessageResponse thankYouMessage2Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(1);
        final long actualThankYouMessage2Id = thankYouMessage2Response.getId();
        final long thankYouMessage2MemberId = thankYouMessage2Response.getMemberResponse()
                .getId();
        final String thankYouMessage2Content = thankYouMessage2Response.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(actualThankYouMessage1Id).isEqualTo(thankYouMessage2Id),
                () -> assertThat(thankYouMessage1MemberId).isEqualTo(member2.getId()),
                () -> assertThat(thankYouMessage1Content).isEqualTo("thankYouMessageContent2"),
                () -> assertThat(actualThankYouMessage2Id).isEqualTo(thankYouMessage1Id),
                () -> assertThat(thankYouMessage2MemberId).isEqualTo(member1.getId()),
                () -> assertThat(thankYouMessage2Content).isEqualTo("thankYouMessageContent1")
        );
    }

    @Test
    void 로그인을_하고_감사_메세지를_저장한다() {
        // given
        final Member member = createMember("test1");
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "http://localhost:8888/api/login?code=testCode",
                LoginResponse.class
        ).getBody();

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        final String accessToken = loginResponse.getAccessToken();
        httpHeaders.setBearerAuth(accessToken);
        final ThankYouMessageRequest thankYouMessageRequest = new ThankYouMessageRequest("thankYouMessageContent");
        final HttpEntity<ThankYouMessageRequest> thankYouMessageRequestHttpEntity = new HttpEntity<>(
                thankYouMessageRequest, httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "http://localhost:8888/api/members/" + member.getId() + "/thankYouMessages",
                HttpMethod.POST,
                thankYouMessageRequestHttpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_감사메세지를_삭제한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();
        final String authorizationHeader = TOKEN_TYPE + " " + loginResponse.getAccessToken();
        final Long memberId = accessTokenProvider.getMemberId(authorizationHeader);
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(new Member(memberId))
                .recipientId(10L)
                .content("thankYouMessageContent")
                .build();
        thankYouMessageRepository.save(thankYouMessage);

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        final HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        final Long thankYouMessageId = thankYouMessage.getId();
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/thankYouMessages/" + thankYouMessageId,
                HttpMethod.DELETE,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_감사메세지를_삭제한다_감사메세지가_존재하지_않는_경우() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();

        // when
        final String authorizationHeader = TOKEN_TYPE + " " + loginResponse.getAccessToken();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        final HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.exchange(
                "/api/thankYouMessages/1",
                HttpMethod.DELETE,
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final String message = responseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(message).isEqualTo("감사메세지 아이디가 존재하지 않아 삭제에 실패했습니다.")
        );
    }

    @Test
    void 로그인을_하고_감사메세지를_삭제한다_내가_작성한_감사메세지가_아니면_예외가_발생한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();
        final Member otherMember = createMember("test1");
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(otherMember)
                .recipientId(1L)
                .content("thankYouMessageContent")
                .build();
        thankYouMessageRepository.save(thankYouMessage);

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        final String accessToken = loginResponse.getAccessToken();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        final Long thankYouMessageId = thankYouMessage.getId();
        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.exchange(
                "/api/thankYouMessages/" + thankYouMessageId,
                HttpMethod.DELETE,
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final String message = responseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(message).isEqualTo("감사메세지 아이디가 존재하지 않아 삭제에 실패했습니다.")
        );
    }
}
