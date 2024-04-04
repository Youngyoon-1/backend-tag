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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommentService {

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

    @Transactional
    public void saveComment(final Long thankYouMessageId, final Long memberId, final String content) {
        if (content.length() > 400) {
            throw new RuntimeException("답글의 길이는 400자 이하여야 합니다.");
        }
        if (!thankYouMessageRepository.existsById(thankYouMessageId)) {
            throw new RuntimeException("존재하지 않는 감사메세지 아이디입니다.");
        }
        final Comment comment = Comment.builder()
                .thankYouMessageId(thankYouMessageId)
                .member(new Member(memberId))
                .content(content)
                .build();
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(final Long commentId, final Long memberId) {
        if (!commentRepository.existsByIdAndMemberId(commentId, memberId)) {
            throw new RuntimeException("댓글 아이디가 유효하지 않습니다.");
        }
        commentRepository.deleteById(commentId);
    }

    public CommentsResponse findComments(final Long thankYouMessageId, final Long pageSize, final Long cursor) {
        // validate pageSize
        if (pageSize > 20) {
            throw new RuntimeException("유효하지 않은 pageSize 값입니다.");
        }

        final List<Comment> comments = commentRepository.findPage(thankYouMessageId, pageSize + 1, cursor);
        final int selectedSize = comments.size();

        if (selectedSize == pageSize + 1) {
            comments.remove(selectedSize - 1);
            final Long newCursor = comments.get(selectedSize - 2)
                    .getId();
            final List<CommentResponse> commentResponses = getCommentResponses(comments);
            return new CommentsResponse(newCursor, commentResponses);
        }

        final List<CommentResponse> commentResponses = getCommentResponses(comments);
        return new CommentsResponse(null, commentResponses);
    }

    private List<CommentResponse> getCommentResponses(final List<Comment> comments) {
        return comments.stream()
                .map(it -> CommentResponse.from(it,
                        getUrl(it.getMember().getProfileImageName())))
                .collect(Collectors.toList());
    }

    private String getUrl(final String imageName) {
        if (imageName != null) {
            return objectStorageManager.createPresignedGetUrl(imageName,
                    MemberImageCategory.PROFILE);
        }
        return null;
    }

    public CommentCountResponse findCommentCount(final Long thankYouMessageId) {
        final Long count = commentRepository.countByThankYouMessageId(thankYouMessageId);
        return new CommentCountResponse(count);
    }
}
