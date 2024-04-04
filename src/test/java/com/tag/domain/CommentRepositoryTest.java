package com.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_존재하는_경우() {
        // given
        final Comment comment = Comment.builder()
                .memberId(10L)
                .thankYouMessageId(2L)
                .content("commentContent")
                .build();
        final Long commentId = commentRepository.save(comment)
                .getId();

        // when
        final Boolean exists = commentRepository.existsByIdAndMemberId(commentId, 10L);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 댓글_아이디와_회원_아이디로_댓글_존재_여부를_확인한다_존재하지_않는_경우() {
        // given
        final Comment comment = Comment.builder()
                .memberId(10L)
                .thankYouMessageId(2L)
                .content("commentContent")
                .build();
        final Long commentId = commentRepository.save(comment)
                .getId();

        // when
        final Boolean exists = commentRepository.existsByIdAndMemberId(commentId, 1L);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 댓글_목록을_조회한다() {
        // given
        final Comment comment1 = Comment.builder()
                .memberId(10L)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .memberId(10L)
                .content("comment2")
                .thankYouMessageId(6L)
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .memberId(10L)
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
        final Comment comment1 = Comment.builder()
                .memberId(10L)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .memberId(10L)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);
        final Comment comment3 = Comment.builder()
                .memberId(10L)
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
                        .isEqualTo(comment3),
                () -> assertThat(actualComment2).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(comment2)
        );
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() {
        // given
        final Comment comment1 = Comment.builder()
                .memberId(10L)
                .content("comment1")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment1);
        final Comment comment2 = Comment.builder()
                .memberId(10L)
                .content("comment2")
                .thankYouMessageId(5L)
                .build();
        commentRepository.save(comment2);

        // when
        final Long count = commentRepository.countByThankYouMessageId(5L);

        // then
        assertThat(count).isEqualTo(2L);
    }
}
