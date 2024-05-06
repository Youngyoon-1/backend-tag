package com.tag.acceptance;

import static com.tag.application.AccessTokenProvider.TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import com.tag.application.AccessTokenProvider;
import com.tag.domain.Comment;
import com.tag.domain.CommentRepository;
import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
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

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 로그인을_하고_댓글을_저장한다() {
        // given
        final Member thankYouMessageWriter = createMember("test1");
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(thankYouMessageWriter)
                .recipientId(1L)
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
        final CommentRequest commentRequest = new CommentRequest("a".repeat(400));
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
    void 로그인을_하고_댓글을_저장한다_댓글의_길이가_401자_이상이면_길면_예외가_발생한다() {
        // given
        final Member thankYouMessageWriter = createMember("test1");
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(thankYouMessageWriter)
                .recipientId(1L)
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
        final CommentRequest commentRequest = new CommentRequest("a".repeat(401));
        final HttpEntity<CommentRequest> httpEntity = new HttpEntity<>(commentRequest, httpHeaders);
        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.postForEntity(
                "/api/thankYouMessages/" + thankYouMessageId + "/comments",
                httpEntity,
                ExceptionResponse.class
        );

        // then
        final HttpStatusCode statusCode = responseEntity.getStatusCode();
        final String exceptionMessage = responseEntity.getBody()
                .getMessage();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                () -> assertThat(exceptionMessage).isEqualTo("댓글의 길이는 400자 이하여야 합니다.")
        );
    }

    private Member createMember(final String email) {
        return memberRepository.save(Member.builder().email(email).build());
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
                "/api/thankYouMessages/10000/comments",
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
                .member(new Member(memberId))
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
        final Member otherMember = createMember("test1");
        final Comment comment = Comment.builder()
                .member(otherMember)
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
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Member member3 = createMember("test3");
        final Comment comment1 = Comment.builder()
                .thankYouMessageId(5L)
                .member(member1)
                .content("comment1")
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .thankYouMessageId(10L)
                .member(member2)
                .content("comment2")
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .thankYouMessageId(5L)
                .member(member3)
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
        final List<CommentResponse> commentsResponses = commentsResponseEntity.getBody()
                .getCommentResponses();
        final int size = commentsResponses.size();
        final CommentResponse commentResponse1 = commentsResponses.get(0);
        final long commentId1 = commentResponse1.getId();
        final long commentId1Expectation = comment3.getId();
        final long memberId1 = commentResponse1.getMemberResponse()
                .getId();
        final String content1 = commentResponse1.getContent();
        final CommentResponse commentResponse2 = commentsResponses.get(1);
        final long commentId2 = commentResponse2.getId();
        final long commentId2Expectation = comment1.getId();
        final long memberId2 = commentResponse2.getMemberResponse()
                .getId();
        final String content2 = commentResponse2.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(size).isEqualTo(2),
                () -> assertThat(commentId1).isEqualTo(commentId1Expectation),
                () -> assertThat(memberId1).isEqualTo(member3.getId()),
                () -> assertThat(content1).isEqualTo("comment3"),
                () -> assertThat(commentId2).isEqualTo(commentId2Expectation),
                () -> assertThat(memberId2).isEqualTo(member1.getId()),
                () -> assertThat(content2).isEqualTo("comment1")
        );
    }

    @Test
    void 댓글_목록을_조회한다_조회가_시작될_댓글_아이디가_주어진_경우() {
        // given
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Member member3 = createMember("test3");
        final Comment comment1 = Comment.builder()
                .thankYouMessageId(5L)
                .member(member1)
                .content("comment1")
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .thankYouMessageId(5L)
                .member(member2)
                .content("comment2")
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .thankYouMessageId(5L)
                .member(member3)
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
        final List<CommentResponse> commentsResponses = commentsResponseEntity.getBody()
                .getCommentResponses();
        final int size = commentsResponses.size();
        final CommentResponse commentResponse1 = commentsResponses.get(0);
        final long commentId1 = commentResponse1.getId();
        final long commentId1Expectation = comment2.getId();
        final long memberId1 = commentResponse1.getMemberResponse()
                .getId();
        final String content1 = commentResponse1.getContent();
        final CommentResponse commentResponse2 = commentsResponses.get(1);
        final long commentId2 = commentResponse2.getId();
        final long commentId2Expectation = comment1.getId();
        final long memberId2 = commentResponse2.getMemberResponse()
                .getId();
        final String content2 = commentResponse2.getContent();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(size).isEqualTo(2),
                () -> assertThat(commentId1).isEqualTo(commentId1Expectation),
                () -> assertThat(memberId1).isEqualTo(member2.getId()),
                () -> assertThat(content1).isEqualTo("comment2"),
                () -> assertThat(commentId2).isEqualTo(commentId2Expectation),
                () -> assertThat(memberId2).isEqualTo(member1.getId()),
                () -> assertThat(content2).isEqualTo("comment1")
        );
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Comment comment1 = Comment.builder()
                .thankYouMessageId(10L)
                .content("comment1")
                .member(member1)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .thankYouMessageId(10L)
                .content("comment2")
                .member(member2)
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
