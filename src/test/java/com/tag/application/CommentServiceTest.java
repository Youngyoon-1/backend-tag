package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import com.tag.application.comment.CommentService;
import com.tag.application.image.ObjectStorageManager;
import com.tag.domain.comment.Comment;
import com.tag.domain.comment.CommentRepository;
import com.tag.domain.member.Member;
import com.tag.domain.thankYouMessage.ThankYouMessageRepository;
import com.tag.dto.response.comment.CommentCountResponse;
import com.tag.dto.response.comment.CommentResponse;
import com.tag.dto.response.comment.CommentsResponse;
import java.util.ArrayList;
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

    @Mock
    private ObjectStorageManager objectStorageManager;

    @InjectMocks
    private CommentService commentService;

    @Test
    void 댓글을_저장한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsById(1L))
                .willReturn(true);

        // when
        commentService.saveComment(1L, 10L, "commentContent");

        // then
        BDDMockito.verify(commentRepository)
                .save(any(Comment.class));
    }

    @Test
    void 댓글을_저장한다_감사메세지가_존재하지_않는_경우_예외가_발생한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsById(1L))
                .willReturn(false);

        // when, then
        assertThatThrownBy(
                () -> commentService.saveComment(1L, 10L, "commentContent")
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 감사메세지에 답글을 저장할 수 없습니다.");
    }

    @Test
    void 댓글을_삭제한다() {
        // given
        BDDMockito.given(commentRepository.existsByIdAndMemberId(1L, 10L))
                .willReturn(true);

        // when
        commentService.deleteComment(1L, 10L);

        // then
        BDDMockito.verify(commentRepository)
                .deleteById(1L);
    }

    @Test
    void 댓글을_삭제한다_자신이_작성한_댓글이_아닌_경우_예외가_발생한다() {
        // given
        BDDMockito.given(commentRepository.existsByIdAndMemberId(1L, 10L))
                .willReturn(false);

        // when, then
        assertThatThrownBy(
                () -> commentService.deleteComment(1L, 10L)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("답글이 존재하지 않거나 작성자가 아니기 때문에 답글을 삭제할 수 없습니다.");
    }

    @Test
    void 댓글_페이지를_조회힌다_마지막_페이지인_경우() {
        // given
        final Comment comment = Comment.builder()
                .id(1L)
                .thankYouMessageId(1L)
                .content("comment")
                .member(new Member(1L))
                .build();
        BDDMockito.given(commentRepository.findPage(1L, 2L, null))
                .willReturn(List.of(comment));
        BDDMockito.given(objectStorageManager.createGetUrl(any()))
                .willReturn("profileImageUrl");

        // when
        final CommentsResponse commentsResponse = commentService.findComments(1L, 1L, null);

        // then
        final Long cursor = commentsResponse.cursor();
        final List<CommentResponse> commentResponses = commentsResponse.commentResponses();
        final int size = commentResponses.size();
        final CommentResponse commentResponse = commentResponses.get(0);
        final long commentId = commentResponse.id();
        final long memberId = commentResponse.memberResponse()
                .id();
        final String profileUrl = commentResponse.memberResponse()
                .profileUrl();
        Assertions.assertAll(
                () -> assertThat(cursor).isNull(),
                () -> assertThat(size).isOne(),
                () -> assertThat(commentId).isNotNull(),
                () -> assertThat(memberId).isEqualTo(1L),
                () -> assertThat(profileUrl).isEqualTo("profileImageUrl")
        );
    }

    @Test
    void 댓글_페이지를_조회힌다_마지막_페이지가_아닌_경우() {
        // given
        final Member member = Member.builder()
                .id(1L)
                .build();
        final Comment comment1 = Comment.builder()
                .id(1L)
                .thankYouMessageId(1L)
                .content("comment")
                .member(member)
                .build();
        final Comment comment2 = Comment.builder()
                .id(2L)
                .thankYouMessageId(1L)
                .content("comment")
                .member(member)
                .build();
        final List<Comment> comments = new ArrayList<>();
        comments.add(comment2);
        comments.add(comment1);
        BDDMockito.given(commentRepository.findPage(1L, 2L, null))
                .willReturn(comments);
        BDDMockito.given(objectStorageManager.createGetUrl(any()))
                .willReturn("profileImageUrl");

        // when
        final CommentsResponse commentsResponse = commentService.findComments(1L, 1L, null);

        // then
        final Long cursor = commentsResponse.cursor();
        final List<CommentResponse> commentResponses = commentsResponse.commentResponses();
        final int size = commentResponses.size();
        final CommentResponse commentResponse = commentResponses.get(0);
        final long commentId = commentResponse.id();
        final long memberId = commentResponse.memberResponse()
                .id();
        final String profileUrl = commentResponse.memberResponse()
                .profileUrl();
        Assertions.assertAll(
                () -> assertThat(cursor).isEqualTo(2L),
                () -> assertThat(size).isOne(),
                () -> assertThat(commentId).isEqualTo(2L),
                () -> assertThat(memberId).isEqualTo(1L),
                () -> assertThat(profileUrl).isEqualTo("profileImageUrl")
        );
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        BDDMockito.given(commentRepository.countByThankYouMessageId(1L))
                .willReturn(1L);

        // when
        final CommentCountResponse commentCount = commentService.findCommentCount(1L);

        // then
        final long count = commentCount.count();
        assertThat(count).isOne();
    }

    @Test
    void 감사메세지_아이디에_해당하는_댓글을_모두_삭제한다() {
        // when
        commentService.deleteCommentsByThankYouMessageId(1L);

        // then
        BDDMockito.verify(commentRepository)
                .deleteByThankYouMessageId(1L);
    }
}
