package com.tag.presentation.comment;

import com.tag.application.comment.CommentService;
import com.tag.dto.request.comment.CommentRequest;
import com.tag.dto.response.comment.CommentCountResponse;
import com.tag.dto.response.comment.CommentsResponse;
import com.tag.presentation.auth.AccessTokenValue;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public final class CommentController {

    private final CommentService commentService;

    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/thankYouMessages/{thankYouMessageId}/comments")
    public ResponseEntity<Void> saveComment(@PathVariable(name = "thankYouMessageId") final long thankYouMessageId,
                                            @AccessTokenValue final long memberId,
                                            @RequestBody @Valid final CommentRequest commentRequest) {
        final String content = commentRequest.content();
        commentService.saveComment(thankYouMessageId, memberId, content);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable final long commentId,
                                              @AccessTokenValue final long memberId) {
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("/thankYouMessages/{thankYouMessageId}/comments")
    public ResponseEntity<CommentsResponse> findComments(
            @PathVariable(name = "thankYouMessageId") final long thankYouMessageId,
            @RequestParam(name = "pageSize") final long pageSize,
            @RequestParam(name = "cursor", required = false) final Long cursor) {
        final CommentsResponse commentsResponse = commentService.findComments(thankYouMessageId, pageSize, cursor);
        return ResponseEntity.ok(commentsResponse);
    }

    @GetMapping("/thankYouMessages/{thankYouMessageId}/comments/count")
    public ResponseEntity<CommentCountResponse> findCommentCount(
            @PathVariable(name = "thankYouMessageId") final long thankYouMessageId) {
        final CommentCountResponse commentCountResponse = commentService.findCommentCount(thankYouMessageId);
        return ResponseEntity.ok(commentCountResponse);
    }
}
