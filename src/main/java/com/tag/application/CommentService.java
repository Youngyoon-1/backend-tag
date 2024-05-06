package com.tag.application;

import com.tag.domain.Comment;
import com.tag.domain.CommentRepository;
import com.tag.domain.Member;
import com.tag.domain.ThankYouMessageRepository;
import com.tag.dto.response.CommentCountResponse;
import com.tag.dto.response.CommentResponse;
import com.tag.dto.response.CommentsResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private static final String FAIL_SAVE_COMMENT_NO_EXIST_THANK_YOU_MESSAGE = "존재하지 않는 감사메세지에 답글을 저장할 수 없습니다.";
    private static final String FAIL_DELETE_COMMENT_NO_EXIST_COMMENT_OR_NO_AUTHORITY = "답글이 존재하지 않거나 작성자가 아니기 때문에 답글을 삭제할 수 없습니다.";
    private static final String INVALID_COMMENT_PAGE_SIZE = "페이지 사이즈가 20 을 초과할 수 없습니다.";

    private static final int COMMENT_PAGE_SIZE_LIMIT = 20;
    private static final int ONE_CHECK_FOR_LAST_PAGE = 1;
    private static final int ONE_FOR_NEW_CURSOR = 1;

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

    @Transactional(readOnly = true)
    public CommentsResponse findComments(final long thankYouMessageId, final Long pageSize, final Long cursor) {
        if (pageSize > COMMENT_PAGE_SIZE_LIMIT) {
            throw new IllegalArgumentException(INVALID_COMMENT_PAGE_SIZE);
        }
        final long pageSizeForCheckLastPage = pageSize + ONE_CHECK_FOR_LAST_PAGE;
        final List<Comment> comments = commentRepository.findPage(thankYouMessageId, pageSizeForCheckLastPage, cursor);
        final int selectedSize = comments.size();
        if (selectedSize == pageSizeForCheckLastPage) {
            final int lastIndexForRemove = pageSize.intValue();
            comments.remove(lastIndexForRemove);
            final int lastIndexForNewCursor = lastIndexForRemove - ONE_FOR_NEW_CURSOR;
            final long newCursor = comments.get(lastIndexForNewCursor)
                    .getId();
            final List<CommentResponse> commentResponses = getCommentResponses(comments);
            return new CommentsResponse(newCursor, commentResponses);
        }
        final List<CommentResponse> commentResponses = getCommentResponses(comments);
        return new CommentsResponse(null, commentResponses);
    }

    private List<CommentResponse> getCommentResponses(final List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentResponse.of(
                        comment,
                        objectStorageManager.createGetUrl(
                                comment.getMember()
                                        .getProfileImageName(),
                                MemberImageCategory.PROFILE)
                )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentCountResponse findCommentCount(final Long thankYouMessageId) {
        final long count = commentRepository.countByThankYouMessageId(thankYouMessageId);
        return new CommentCountResponse(count);
    }

    @Async
    public void deleteCommentsByThankYouMessageId(final Long thankYouMessageId) {
        commentRepository.deleteByThankYouMessageId(thankYouMessageId);
    }
}
