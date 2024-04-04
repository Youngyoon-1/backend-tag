package com.tag.acceptance;

import static com.tag.application.AccessTokenProvider.TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import com.tag.application.AccessTokenProvider;
import com.tag.domain.Comment;
import com.tag.domain.CommentRepository;
import com.tag.domain.ThankYouMessage;
import com.tag.domain.ThankYouMessageRepository;
import com.tag.dto.request.CommentRequest;
import com.tag.dto.response.CommentCountResponse;
import com.tag.dto.response.CommentResponse;
import com.tag.dto.response.CommentsResponse;
import com.tag.dto.response.ExceptionResponse;
import com.tag.dto.response.LoginResponse;
import java.util.List;
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
public class CommentAcceptanceTest extends WithTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ThankYouMessageRepository thankYouMessageRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AccessTokenProvider accessTokenProvider;

    @Test
    void 로그인을_하고_댓글을_저장한다() {
        // given
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .memberId(100L)
                .content("thankYouMessageContent")
                .build();
        final Long thankYouMessageId = thankYouMessageRepository.save(thankYouMessage)
                .getId();
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        final String accessToken = loginResponse.getAccessToken();
        httpHeaders.setBearerAuth(accessToken);
        final CommentRequest commentRequest = new CommentRequest("commentContent");
        final HttpEntity<CommentRequest> httpEntity = new HttpEntity<>(commentRequest, httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.postForEntity(
                "/api/thankYouMessages/" + thankYouMessageId + "/comments",
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_댓글을_저장한다_존재하지_않는_감사메세지인_경우() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        final String accessToken = loginResponse.getAccessToken();
        httpHeaders.setBearerAuth(accessToken);
        final CommentRequest commentRequest = new CommentRequest("commentContent");
        final HttpEntity<CommentRequest> httpEntity = new HttpEntity<>(commentRequest, httpHeaders);
        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.postForEntity(
                "/api/thankYouMessages/1/comments",
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final String message = responseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(message).isEqualTo("존재하지 않는 감사메세지 아이디입니다.")
        );
    }

    @Test
    void 로그인을_하고_댓글을_삭제한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();
        final String accessToken = loginResponse.getAccessToken();
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;
        final Long memberId = accessTokenProvider.getMemberId(authorizationHeader);
        final Comment comment = Comment.builder()
                .memberId(memberId)
                .thankYouMessageId(1L)
                .content("commentContent")
                .build();
        final Long commentId = commentRepository.save(comment)
                .getId();

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        final ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                "/api/comments/" + commentId,
                HttpMethod.DELETE,
                httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void 로그인을_하고_댓글을_삭제한다_자신이_작성한_댓글이_아닌_경우() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=testCode",
                LoginResponse.class
        ).getBody();
        final Comment comment = Comment.builder()
                .memberId(1000L)
                .thankYouMessageId(1L)
                .content("commentContent")
                .build();
        final Long commentId = commentRepository.save(comment)
                .getId();

        // when
        final HttpHeaders httpHeaders = new HttpHeaders();
        final String accessToken = loginResponse.getAccessToken();
        httpHeaders.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.exchange(
                "/api/comments/" + commentId,
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
                () -> assertThat(message).isEqualTo("댓글 아이디가 유효하지 않습니다.")
        );
    }

    @Test
    void 댓글_목록을_조회한다() {
        // given
        final Comment comment1 = Comment.builder()
                .thankYouMessageId(5L)
                .memberId(1L)
                .content("comment1")
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .thankYouMessageId(10L)
                .memberId(2L)
                .content("comment2")
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .thankYouMessageId(5L)
                .memberId(3L)
                .content("comment3")
                .build();
        commentRepository.save(comment3);

        // when
        final ResponseEntity<CommentsResponse> commentsResponseEntity = testRestTemplate.getForEntity(
                "/api/thankYouMessages/5/comments?pageSize=2",
                CommentsResponse.class
        );

        // then
        final HttpStatusCode statusCode = commentsResponseEntity.getStatusCode();
        final List<CommentResponse> commentsResponse = commentsResponseEntity.getBody()
                .getCommentsResponse();
        final int size = commentsResponse.size();
        final CommentResponse commentResponse1 = commentsResponse.get(0);
        final long commentId1 = commentResponse1.getId();
        final long commentId1Expectation = comment3.getId();
        final long memberId1 = commentResponse1.getMemberId();
        final String content1 = commentResponse1.getContent();
        final CommentResponse commentResponse2 = commentsResponse.get(1);
        final long commentId2 = commentResponse2.getId();
        final long commentId2Expectation = comment1.getId();
        final long memberId2 = commentResponse2.getMemberId();
        final String content2 = commentResponse2.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(size).isEqualTo(2),
                () -> assertThat(commentId1).isEqualTo(commentId1Expectation),
                () -> assertThat(memberId1).isEqualTo(3L),
                () -> assertThat(content1).isEqualTo("comment3"),
                () -> assertThat(commentId2).isEqualTo(commentId2Expectation),
                () -> assertThat(memberId2).isEqualTo(1L),
                () -> assertThat(content2).isEqualTo("comment1")
        );
    }

    @Test
    void 댓글_목록을_조회한다_조회가_시작될_댓글_아이디가_주어진_경우() {
        // given
        final Comment comment1 = Comment.builder()
                .thankYouMessageId(5L)
                .memberId(1L)
                .content("comment1")
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .thankYouMessageId(5L)
                .memberId(2L)
                .content("comment2")
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .thankYouMessageId(5L)
                .memberId(3L)
                .content("comment3")
                .build();
        commentRepository.save(comment3);

        // when
        final ResponseEntity<CommentsResponse> commentsResponseEntity = testRestTemplate.getForEntity(
                "/api/thankYouMessages/5/comments?pageSize=2&cursor=" + comment3.getId(),
                CommentsResponse.class
        );

        // then
        final HttpStatusCode statusCode = commentsResponseEntity.getStatusCode();
        final List<CommentResponse> commentsResponse = commentsResponseEntity.getBody()
                .getCommentsResponse();
        final int size = commentsResponse.size();
        final CommentResponse commentResponse1 = commentsResponse.get(0);
        final long commentId1 = commentResponse1.getId();
        final long commentId1Expectation = comment3.getId();
        final long memberId1 = commentResponse1.getMemberId();
        final String content1 = commentResponse1.getContent();
        final CommentResponse commentResponse2 = commentsResponse.get(1);
        final long commentId2 = commentResponse2.getId();
        final long commentId2Expectation = comment2.getId();
        final long memberId2 = commentResponse2.getMemberId();
        final String content2 = commentResponse2.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(size).isEqualTo(2),
                () -> assertThat(commentId1).isEqualTo(commentId1Expectation),
                () -> assertThat(memberId1).isEqualTo(3L),
                () -> assertThat(content1).isEqualTo("comment3"),
                () -> assertThat(commentId2).isEqualTo(commentId2Expectation),
                () -> assertThat(memberId2).isEqualTo(2L),
                () -> assertThat(content2).isEqualTo("comment2")
        );
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        final Comment comment1 = Comment.builder()
                .thankYouMessageId(10L)
                .content("comment1")
                .memberId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .thankYouMessageId(10L)
                .content("comment2")
                .memberId(6L)
                .build();
        commentRepository.save(comment2);

        // when
        final ResponseEntity<CommentCountResponse> commentCountResponseEntity = testRestTemplate.getForEntity(
                "/api/thankYouMessages/10/comments/count",
                CommentCountResponse.class
        );

        // then
        final HttpStatusCode statusCode = commentCountResponseEntity.getStatusCode();
        final long count = commentCountResponseEntity.getBody()
                .getCount();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(count).isEqualTo(2L)
        );
    }
}
