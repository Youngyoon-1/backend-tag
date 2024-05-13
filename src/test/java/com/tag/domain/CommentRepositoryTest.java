package com.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.comment.Comment;
import com.tag.domain.comment.CommentRepository;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
        final Member member = createMember("test1");
        final Comment comment = Comment.builder()
                .member(member)
                .thankYouMessageId(2L)
                .content("commentContent")
                .build();
        final Long commentId = commentRepository.save(comment)
                .getId();

        // when
        final Boolean exists = commentRepository.existsByIdAndMemberId(commentId, member.getId());

        // then
        assertThat(exists).isTrue();
    }

    private Member createMember(final String email) {
        return memberRepository.save(Member.builder().email(email).build());
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_회원_아이디가_존재하지_않는_경우() {
        // given
        final Member member = createMember("test1");
        final Comment comment = Comment.builder()
                .member(member)
                .thankYouMessageId(2L)
                .content("commentContent")
                .build();
        final Long commentId = commentRepository.save(comment)
                .getId();

        // when
        final int notExistMemberId = 100;
        final Boolean exists = commentRepository.existsByIdAndMemberId(commentId, notExistMemberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_댓글_아이디가_존재하지_않는_경우() {
        // given
        final long memberId = createMember("test1")
                .getId();

        // when
        final int notExistCommentId = 100;
        final Boolean exists = commentRepository.existsByIdAndMemberId(notExistCommentId, memberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_두_가지_모두_존재하지_않는_경우() {
        // given
        // when
        final int notExistCommentId = 100;
        final int notExistMemberId = 100;
        final Boolean exists = commentRepository.existsByIdAndMemberId(notExistCommentId, notExistMemberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_목록을_조회한다() {
        // given
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Member member3 = createMember("test3");
        final Comment comment1 = Comment.builder()
                .member(member1)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member2)
                .content("comment2")
                .thankYouMessageId(6L)
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .member(member3)
                .content("comment3")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment3);

        // when
        final List<Comment> comments = commentRepository.findPage(5L, 2L, null);

        // then
        final int size = comments.size();
        final Comment actualComment1 = comments.get(0);
        final Comment actualComment2 = comments.get(1);
        Assertions.assertAll(
                () -> assertThat(size).isEqualTo(2),
                () -> assertThat(actualComment1).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(comment3),
                () -> assertThat(actualComment2).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(comment1)
        );
    }

    @Test
    void 댓글_목록을_조회한다_조회를_시작할_댓글_아이디가_주어진_경우() {
        // given
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Member member3 = createMember("test3");
        final Comment comment1 = Comment.builder()
                .member(member1)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member2)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .member(member3)
                .content("comment3")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment3);

        // when
        final Long fromId = comment3.getId();
        final List<Comment> comments = commentRepository.findPage(5L, 2L, fromId);

        // then
        final int size = comments.size();
        final Comment actualComment1 = comments.get(0);
        final Comment actualComment2 = comments.get(1);
        Assertions.assertAll(
                () -> assertThat(size).isEqualTo(2),
                () -> assertThat(actualComment1).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(comment2),
                () -> assertThat(actualComment2).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(comment1)
        );
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Comment comment1 = Comment.builder()
                .member(member1)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .member(member2)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);

        // when
        final Long count = commentRepository.countByThankYouMessageId(5L);

        // then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다_감사메세지가_존재하지_않는_경우() {
        // when
        final Long count = commentRepository.countByThankYouMessageId(1L);

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    void 답글을_저장한다_답글의_길이가_401자_이상이면_예외가_발생한다() {
        // given
        final Member member1 = createMember("test1");
        final Comment comment1 = Comment.builder()
                .member(member1)
                .content("a".repeat(401))
                .thankYouMessageId(5L)
                .build();

        // when
        final Comment save = commentRepository.save(comment1);

        // then
        final Long id = save.getId();
    }
}
