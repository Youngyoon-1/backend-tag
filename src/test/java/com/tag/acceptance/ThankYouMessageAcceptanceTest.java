package com.tag.acceptance;

import static com.tag.application.AccessTokenProvider.TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import com.tag.application.AccessTokenProvider;
import com.tag.domain.ThankYouMessage;
import com.tag.domain.ThankYouMessageRepository;
import com.tag.dto.request.ThankYouMessageRequest;
import com.tag.dto.response.ExceptionResponse;
import com.tag.dto.response.LoginResponse;
import com.tag.dto.response.ThankYouMessageResponse;
import com.tag.dto.response.ThankYouMessagesResponse;
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

    @Test
    void 감사_메세지_2_개를_조회한다() {
        // given
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .memberId(10L)
                .content("thankYouMessageContent1")
                .build();
        final Long thankYouMessage1Id = thankYouMessageRepository.save(thankYouMessage1)
                .getId();
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .memberId(10L)
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
        final long thankYouMessage1MemberId = thankYouMessage1Response.getMemberId();
        final String thankYouMessage1Content = thankYouMessage1Response.getContent();
        final ThankYouMessageResponse thankYouMessage2Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(1);
        final long actualThankYouMessage2Id = thankYouMessage2Response.getId();
        final long thankYouMessage2MemberId = thankYouMessage2Response.getMemberId();
        final String thankYouMessage2Content = thankYouMessage2Response.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(actualThankYouMessage1Id).isEqualTo(thankYouMessage2Id),
                () -> assertThat(thankYouMessage1MemberId).isEqualTo(10L),
                () -> assertThat(thankYouMessage1Content).isEqualTo("thankYouMessageContent2"),
                () -> assertThat(actualThankYouMessage2Id).isEqualTo(thankYouMessage1Id),
                () -> assertThat(thankYouMessage2MemberId).isEqualTo(10L),
                () -> assertThat(thankYouMessage2Content).isEqualTo("thankYouMessageContent1")
        );
    }

    @Test
    void 감사_메세지_2_개를_조회한다_조회가_시작될_아이디가_주어진_경우() {
        // given
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .memberId(10L)
                .content("thankYouMessageContent1")
                .build();
        final Long thankYouMessage1Id = thankYouMessageRepository.save(thankYouMessage1)
                .getId();
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .memberId(10L)
                .content("thankYouMessageContent2")
                .build();
        final Long thankYouMessage2Id = thankYouMessageRepository.save(thankYouMessage2)
                .getId();
        final ThankYouMessage thankYouMessage3 = ThankYouMessage.builder()
                .memberId(10L)
                .content("thankYouMessageContent3")
                .build();
        thankYouMessageRepository.save(thankYouMessage3);

        // when
        final ResponseEntity<ThankYouMessagesResponse> thankYouMessagesResponseEntity = testRestTemplate.getForEntity(
                "/api/members/10/thankYouMessages?pageSize=2&cursor=" + thankYouMessage2Id,
                ThankYouMessagesResponse.class
        );

        // then
        final HttpStatusCode statusCode = thankYouMessagesResponseEntity.getStatusCode();
        final ThankYouMessageResponse thankYouMessage1Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(0);
        final long actualThankYouMessage1Id = thankYouMessage1Response.getId();
        final long thankYouMessage1MemberId = thankYouMessage1Response.getMemberId();
        final String thankYouMessage1Content = thankYouMessage1Response.getContent();
        final ThankYouMessageResponse thankYouMessage2Response = thankYouMessagesResponseEntity.getBody()
                .getThankYouMessageResponses()
                .get(1);
        final long actualThankYouMessage2Id = thankYouMessage2Response.getId();
        final long thankYouMessage2MemberId = thankYouMessage2Response.getMemberId();
        final String thankYouMessage2Content = thankYouMessage2Response.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(actualThankYouMessage1Id).isEqualTo(thankYouMessage2Id),
                () -> assertThat(thankYouMessage1MemberId).isEqualTo(10L),
                () -> assertThat(thankYouMessage1Content).isEqualTo("thankYouMessageContent2"),
                () -> assertThat(actualThankYouMessage2Id).isEqualTo(thankYouMessage1Id),
                () -> assertThat(thankYouMessage2MemberId).isEqualTo(10L),
                () -> assertThat(thankYouMessage2Content).isEqualTo("thankYouMessageContent1")
        );
    }

    @Test
    void 로그인을_하고_감사_메세지를_저장한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
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
                "/api/thankYouMessages",
                HttpMethod.POST,
                thankYouMessageRequestHttpEntity,
                Void.class
        );

        // when
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
                .memberId(memberId)
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
                () -> assertThat(message).isEqualTo("감사메세지 아이디가 유효하지 않습니다.")
        );
    }

    @Test
    void 로그인을_하고_감사메세지를_삭제한다_내가_작성한_감사메세지가_아니면_예외가_발생한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .memberId(100L)
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
                () -> assertThat(message).isEqualTo("감사메세지 아이디가 유효하지 않습니다.")
        );
    }
}
