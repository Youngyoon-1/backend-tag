package com.tag.presentation;

import com.tag.application.CommentService;
import com.tag.dto.request.CommentRequest;
import com.tag.dto.response.CommentCountResponse;
import com.tag.dto.response.CommentsResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class CommentController {

    private final CommentService commentService;

    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/api/thankYouMessages/{thankYouMessageId}/comments")
    public ResponseEntity<Void> saveComment(@PathVariable(name = "thankYouMessageId") final long thankYouMessageId,
                                            @AccessTokenValue final long memberId,
                                            @RequestBody @Valid final CommentRequest commentRequest) {
        final String content = commentRequest.getContent();
        commentService.saveComment(thankYouMessageId, memberId, content);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable final long commentId,
                                              @AccessTokenValue final long memberId) {
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/api/thankYouMessages/{thankYouMessageId}/comments")
    public ResponseEntity<CommentsResponse> findComments(
            @PathVariable(name = "thankYouMessageId") final long thankYouMessageId,
            @RequestParam(name = "pageSize") final Long pageSize,
            @RequestParam(name = "cursor", required = false) final Long cursor) {
        final CommentsResponse commentsResponse = commentService.findComments(thankYouMessageId, pageSize, cursor);
        return ResponseEntity.ok(commentsResponse);
    }

    @GetMapping("/api/thankYouMessages/{thankYouMessageId}/comments/count")
    public ResponseEntity<CommentCountResponse> findCommentCount(
            @PathVariable(name = "thankYouMessageId") final long thankYouMessageId) {
        final CommentCountResponse commentCountResponse = commentService.findCommentCount(thankYouMessageId);
        return ResponseEntity.ok(commentCountResponse);
    }
}
