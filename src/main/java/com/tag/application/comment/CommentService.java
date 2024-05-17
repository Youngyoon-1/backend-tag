package com.tag.application.comment;

import com.tag.application.image.ObjectStorageManager;
import com.tag.application.pagination.PageableService;
import com.tag.domain.comment.Comment;
import com.tag.domain.comment.CommentRepository;
import com.tag.domain.member.Member;
import com.tag.domain.thankYouMessage.ThankYouMessageRepository;
import com.tag.dto.response.comment.CommentCountResponse;
import com.tag.dto.response.comment.CommentResponse;
import com.tag.dto.response.comment.CommentsResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService extends PageableService<Comment, CommentsResponse> {

    private static final String FAIL_SAVE_COMMENT_NO_EXIST_THANK_YOU_MESSAGE = "존재하지 않는 감사메세지에 답글을 저장할 수 없습니다.";
    private static final String FAIL_DELETE_COMMENT_NO_EXIST_COMMENT_OR_NO_AUTHORITY = "답글이 존재하지 않거나 작성자가 아니기 때문에 답글을 삭제할 수 없습니다.";

    private final CommentRepository commentRepository;
    private final ThankYouMessageRepository thankYouMessageRepository;
    private final ObjectStorageManager objectStorageManager;

    public CommentService(final CommentRepository commentRepository,
                          final ThankYouMessageRepository thankYouMessageRepository,
                          final ObjectStorageManager objectStorageManager) {
        this.commentRepository = commentRepository;
        this.thankYouMessageRepository = thankYouMessageRepository;
        this.objectStorageManager = objectStorageManager;
    }

    public void saveComment(final long thankYouMessageId, final long memberId, final String content) {
        // TODO: Comment 테이블 외래키 추가
        if (!thankYouMessageRepository.existsById(thankYouMessageId)) {
            throw new IllegalArgumentException(FAIL_SAVE_COMMENT_NO_EXIST_THANK_YOU_MESSAGE);
        }
        final Comment comment = Comment.builder()
                .thankYouMessageId(thankYouMessageId)
                .member(Member.createForJPA(memberId))
                .content(content)
                .build();
        commentRepository.save(comment);
    }

    public void deleteComment(final long commentId, final long memberId) {
        if (!commentRepository.existsByIdAndMemberId(commentId, memberId)) {
            throw new IllegalArgumentException(FAIL_DELETE_COMMENT_NO_EXIST_COMMENT_OR_NO_AUTHORITY);
        }
        commentRepository.deleteById(commentId);
    }

    public CommentsResponse findComments(final long thankYouMessageId, final long pageSize, final Long cursor) {
        return findPage(thankYouMessageId, pageSize, cursor, commentRepository);
    }

    @Override
    protected CommentsResponse createResponse(final Long newCursor, final List<Comment> comments) {
        final List<CommentResponse> responses = comments.stream()
                .map(comment -> CommentResponse.of(
                        comment,
                        objectStorageManager.createGetUrl(
                                comment.getMember()
                                        .getProfileImageName())
                )).collect(Collectors.toList());
        return new CommentsResponse(newCursor, responses);
    }

    @Transactional(readOnly = true)
    public CommentCountResponse findCommentCount(final long thankYouMessageId) {
        final long count = commentRepository.countByThankYouMessageId(thankYouMessageId);
        return new CommentCountResponse(count);
    }

    public void deleteCommentsByThankYouMessageId(final long thankYouMessageId) {
        commentRepository.deleteByThankYouMessageId(thankYouMessageId);
    }
}
