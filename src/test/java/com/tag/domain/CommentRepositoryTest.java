package com.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tag.domain.comment.Comment;
import com.tag.domain.comment.CommentRepository;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_존재하는_경우() {
        // given
        final Member member = saveMember();
        final Comment comment = Comment.builder()
                .member(member)
                .thankYouMessageId(2L)
                .content("comment")
                .build();
        final long commentId = commentRepository.save(comment)
                .getId();

        // when
        final boolean exists = commentRepository.existsByIdAndMemberId(commentId, member.getId());

        // then
        assertThat(exists).isTrue();
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().email("test@test.com").build());
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_회원_아이디가_존재하지_않는_경우() {
        // given
        final Member member = saveMember();
        final Comment comment = Comment.builder()
                .member(member)
                .thankYouMessageId(2L)
                .content("comment")
                .build();
        final long commentId = commentRepository.save(comment)
                .getId();
        final int notExistMemberId = 100;

        // when
        final boolean exists = commentRepository.existsByIdAndMemberId(commentId, notExistMemberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_댓글_아이디가_존재하지_않는_경우() {
        // given
        final long memberId = saveMember()
                .getId();
        final int notExistCommentId = 100;

        // when
        final boolean exists = commentRepository.existsByIdAndMemberId(notExistCommentId, memberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_두_가지_모두_존재하지_않는_경우() {
        // given
        final int notExistCommentId = 100;
        final int notExistMemberId = 100;

        // when
        final boolean exists = commentRepository.existsByIdAndMemberId(notExistCommentId, notExistMemberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_페이지를_조회한다_커서가_null_인_경우_가장_큰_댓글_아이디_부터_조회한다() {
        // given
        final Member member = saveMember();
        final Comment comment1 = Comment.builder()
                .member(member)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);

        // when
        final List<Comment> comments = commentRepository.findPage(5L, 2L, null);

        // then
        final int size = comments.size();
        final Comment actualComment1 = comments.get(0);
        final Comment actualComment2 = comments.get(1);
        assertAll(
                () -> assertThat(size).isEqualTo(2),
                // 내림차순 정렬이므로 comment2가 먼저 조회되어야 한다.
                () -> assertThat(actualComment1).usingRecursiveComparison()
                        .isEqualTo(comment2),
                () -> assertThat(actualComment2).usingRecursiveComparison()
                        .isEqualTo(comment1)
        );
    }

    @Test
    void 댓글_목록을_조회한다_커서가_주어진_경우_해당_커서를_댓글_아이디로_간주하고_해당_아이디_보다_작은_댓글들을_조회한다() {
        // given
        final Member member = saveMember();
        final Comment comment1 = Comment.builder()
                .member(member)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);

        // when
        final Long fromId = comment2.getId();
        final List<Comment> comments = commentRepository.findPage(5L, 1L, fromId);

        // then
        final int size = comments.size();
        final Comment actualComment1 = comments.get(0);
        assertAll(
                () -> assertThat(size).isOne(),
                () -> assertThat(actualComment1).usingRecursiveComparison()
                        .isEqualTo(comment1)
        );
    }

    @Test
    void 댓글_목록을_조회한다_페이지_사이즈보다_조회된_개수가_작은_경우_예외가_발생하지_않는다() {
        // given
        final Member member = saveMember();
        final Comment comment = Comment.builder()
                .member(member)
                .content("comment")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment);

        // when
        final List<Comment> comments = commentRepository.findPage(5L, 2L, null);

        // then
        final int size = comments.size();
        assertThat(size).isOne();
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        final Member member = saveMember();
        final Comment comment1 = Comment.builder()
                .member(member)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);

        // when
        final long count = commentRepository.countByThankYouMessageId(5L);

        // then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다_감사메세지가_존재하지_않는_경우() {
        // when
        final long count = commentRepository.countByThankYouMessageId(1L);

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    void 감사메세지_아이디로_댓글을_삭제한다() {
        // given
        final Member member = saveMember();
        final Comment comment1 = Comment.builder()
                .member(member)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);

        // when
        commentRepository.deleteByThankYouMessageId(5L);

        // then
        final Optional<Comment> result1 = commentRepository.findById(comment1.getId());
        final Optional<Comment> result2 = commentRepository.findById(comment2.getId());
        assertAll(
                () -> assertThat(result1).isEmpty(),
                () -> assertThat(result2).isEmpty()
        );
    }

    @Test
    void 감사메세지_아이디로_댓글을_삭제한다_삭제될_댓글이_없는_경우() {
        assertThatNoException()
                .isThrownBy(() -> commentRepository.deleteByThankYouMessageId(5L));
    }
}
