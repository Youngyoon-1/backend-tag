package com.tag.presentation;

import com.tag.application.CommentService;
import com.tag.application.SendMailService;
import com.tag.application.ThankYouMessageService;
import com.tag.dto.request.ThankYouMessageRequest;
import com.tag.dto.response.SaveThankYouMessageResult;
import com.tag.dto.response.ThankYouMessagesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class ThankYouMessageController {

    private final ThankYouMessageService thankYouMessageservice;
    private final SendMailService sendMailService;
    private final CommentService commentService;

    public ThankYouMessageController(final ThankYouMessageService thankYouMessageservice,
                                     final SendMailService sendMailService, final CommentService commentService) {
        this.thankYouMessageservice = thankYouMessageservice;
        this.sendMailService = sendMailService;
        this.commentService = commentService;
    }

    @GetMapping("/api/members/{memberId}/thankYouMessages")
    public ResponseEntity<ThankYouMessagesResponse> findThankYouMessages(
            @PathVariable(name = "memberId") final long memberId,
            @RequestParam(name = "pageSize", required = false) final Long pageSize,
            @RequestParam(name = "cursor", required = false) final Long cursor) {
        final ThankYouMessagesResponse thankYouMessagesResponse = thankYouMessageservice.findThankYouMessages(memberId,
                pageSize, cursor);

        return ResponseEntity.ok(thankYouMessagesResponse);
    }

    @PostMapping("/api/members/{memberId}/thankYouMessages")
    public ResponseEntity<Void> saveThankYouMessage(@AccessTokenValue final long writerMemberId,
                                                    @PathVariable(name = "memberId") final long recipientId,
                                                    @RequestBody final ThankYouMessageRequest thankYouMessageRequest) {
        final String content = thankYouMessageRequest.getContent();
        final SaveThankYouMessageResult saveThankYouMessageResult = thankYouMessageservice.saveThankYouMessage(
                writerMemberId, recipientId, content);
        sendMailService.sendMail(saveThankYouMessageResult);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/api/thankYouMessages/{thankYouMessageId}")
    public ResponseEntity<Void> deleteThankYouMessage(@AccessTokenValue final long memberId,
                                                      @PathVariable(name = "thankYouMessageId") long thankYouMessageId) {

        // Todo: async
        thankYouMessageservice.deleteThankYouMessage(thankYouMessageId, memberId);
        runAsync {
            commentService.deleteCommentsByThankYouMessageId(thankYouMessageId);
        }
        return ResponseEntity.noContent()
                .build();
    }
}
