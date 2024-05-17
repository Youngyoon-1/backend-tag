package com.tag.presentation.thankYouMessage;

import com.tag.application.async.AsyncExecutor;
import com.tag.application.comment.CommentService;
import com.tag.application.mail.SendMailService;
import com.tag.application.thankYouMessage.ThankYouMessageService;
import com.tag.dto.request.thankYouMessage.ThankYouMessageRequest;
import com.tag.dto.response.thankYouMessage.SaveThankYouMessageResult;
import com.tag.dto.response.thankYouMessage.ThankYouMessagesResponse;
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
public final class ThankYouMessageController {

    private final ThankYouMessageService thankYouMessageservice;
    private final SendMailService sendMailService;
    private final CommentService commentService;
    private final AsyncExecutor asyncExecutor;

    public ThankYouMessageController(final ThankYouMessageService thankYouMessageservice,
                                     final SendMailService sendMailService, final CommentService commentService,
                                     final AsyncExecutor asyncExecutor) {
        this.thankYouMessageservice = thankYouMessageservice;
        this.sendMailService = sendMailService;
        this.commentService = commentService;
        this.asyncExecutor = asyncExecutor;
    }

    @GetMapping("/members/{memberId}/thankYouMessages")
    public ResponseEntity<ThankYouMessagesResponse> findThankYouMessages(
            @PathVariable(name = "memberId") final long memberId,
            @RequestParam(name = "pageSize") final long pageSize,
            @RequestParam(name = "cursor", required = false) final Long cursor) {
        final ThankYouMessagesResponse thankYouMessagesResponse = thankYouMessageservice.findThankYouMessages(memberId,
                pageSize, cursor);
        return ResponseEntity.ok(thankYouMessagesResponse);
    }

    @PostMapping("/members/{memberId}/thankYouMessages")
    public ResponseEntity<Void> saveThankYouMessage(@AccessTokenValue final long writerMemberId,
                                                    @PathVariable(name = "memberId") final long recipientId,
                                                    @RequestBody @Valid final ThankYouMessageRequest thankYouMessageRequest) {
        final String content = thankYouMessageRequest.content();
        final SaveThankYouMessageResult saveThankYouMessageResult = thankYouMessageservice.saveThankYouMessage(
                writerMemberId, recipientId, content);
        asyncExecutor.execute(() -> sendMailService.sendMail(saveThankYouMessageResult));
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/thankYouMessages/{thankYouMessageId}")
    public ResponseEntity<Void> deleteThankYouMessage(@AccessTokenValue final long memberId,
                                                      @PathVariable(name = "thankYouMessageId") long thankYouMessageId) {
        thankYouMessageservice.deleteThankYouMessage(thankYouMessageId, memberId);
        asyncExecutor.execute(() -> commentService.deleteCommentsByThankYouMessageId(thankYouMessageId));
        return ResponseEntity.noContent()
                .build();
    }
}
