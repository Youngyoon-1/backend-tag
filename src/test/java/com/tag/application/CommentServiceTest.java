package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.domain.Comment;
import com.tag.domain.CommentRepository;
import com.tag.domain.ThankYouMessageRepository;
import com.tag.dto.response.CommentCountResponse;
import com.tag.dto.response.CommentResponse;
import com.tag.dto.response.CommentsResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ThankYouMessageRepository thankYouMessageRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void 댓글을_저장한다() {
        // given
        BDDMockito.given(commentRepository.save(BDDMockito.any(Comment.class)))
                .willReturn(null);
        BDDMockito.given(thankYouMessageRepository.existsById(1L))
                .willReturn(Boolean.TRUE);

        // when
        commentService.saveComment(1L, 10L, "commentContent");

        // then
        Assertions.assertAll(
                () -> BDDMockito.verify(commentRepository)
                        .save(BDDMockito.any(Comment.class)),
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .existsById(1L)
        );
    }

    @Test
    void 댓글을_저장한다_감사메세지가_존재하지_않는_경우_예외가_발생한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsById(1L))
                .willReturn(Boolean.FALSE);

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> commentService.saveComment(1L, 10L, "commentContent")
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("존재하지 않는 감사메세지 아이디입니다."),
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .existsById(1L)
        );
    }

    @Test
    void 댓글을_삭제한다() {
        // given
        BDDMockito.given(commentRepository.existsByIdAndMemberId(1L, 10L))
                .willReturn(Boolean.TRUE);
        BDDMockito.willDoNothing()
                .given(commentRepository)
                .deleteById(1L);

        // when
        commentService.deleteComment(1L, 10L);

        // then
        Assertions.assertAll(
                () -> BDDMockito.verify(commentRepository)
                        .existsByIdAndMemberId(1L, 10L),
                () -> BDDMockito.verify(commentRepository)
                        .deleteById(1L)
        );
    }

    @Test
    void 댓글을_삭제한다_자신이_작성한_댓글이_아닌_경우_예외가_발생한다() {
        // given
        BDDMockito.given(commentRepository.existsByIdAndMemberId(1L, 10L))
                .willReturn(Boolean.FALSE);

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> commentService.deleteComment(1L, 10L)
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("댓글 아이디가 유효하지 않습니다."),
                () -> BDDMockito.verify(commentRepository)
                        .existsByIdAndMemberId(1L, 10L)
        );
    }

    @Test
    void 댓글_목록을_조회한다() {
        // given
        final Comment comment = Comment.builder()
                .id(1L)
                .thankYouMessageId(10L)
                .content("comment")
                .memberId(10L)
                .build();
        BDDMockito.given(commentRepository.findPage(10L, 1L, 1L))
                .willReturn(List.of(comment));

        // when
        final CommentsResponse commentsResponse = commentService.findComments(10L, 1L, 1L);

        // then
        final List<CommentResponse> commentResponses = commentsResponse.getCommentsResponse();
        final int size = commentResponses.size();
        final CommentResponse commentResponse = commentResponses.get(0);
        final long commentId = commentResponse.getId();
        final String content = commentResponse.getContent();
        final long memberId = commentResponse.getMemberId();
        Assertions.assertAll(
                () -> assertThat(size).isOne(),
                () -> assertThat(commentId).isNotNull(),
                () -> assertThat(content).isEqualTo("comment"),
                () -> assertThat(memberId).isEqualTo(10L),
                () -> BDDMockito.verify(commentRepository)
                        .findPage(10L, 1L, 1L)
        );
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        BDDMockito.given(commentRepository.countByThankYouMessageId(5L))
                .willReturn(1L);

        // when
        final CommentCountResponse commentCount = commentService.findCommentCount(5L);

        // then
        final long count = commentCount.getCount();
        Assertions.assertAll(
                () -> BDDMockito.verify(commentRepository)
                        .countByThankYouMessageId(5L),
                () -> assertThat(count).isOne()
        );
    }
}
