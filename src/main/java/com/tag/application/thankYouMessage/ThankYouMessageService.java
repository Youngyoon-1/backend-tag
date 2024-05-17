package com.tag.application.thankYouMessage;

import com.tag.application.image.ObjectStorageManager;
import com.tag.application.pagination.PageableService;
import com.tag.domain.comment.CommentRepository;
import com.tag.domain.member.Member;
import com.tag.domain.thankYouMessage.ThankYouMessage;
import com.tag.domain.thankYouMessage.ThankYouMessageRepository;
import com.tag.dto.response.thankYouMessage.SaveThankYouMessageResult;
import com.tag.dto.response.thankYouMessage.ThankYouMessageResponse;
import com.tag.dto.response.thankYouMessage.ThankYouMessagesResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ThankYouMessageService extends PageableService<ThankYouMessage, ThankYouMessagesResponse> {

    private static final String NO_EXIST_THANK_YOU_MESSAGE = "감사메세지 아이디가 존재하지 않아 삭제에 실패했습니다.";

    private final ThankYouMessageRepository thankYouMessageRepository;
    private final CommentRepository commentRepository;
    private final ObjectStorageManager objectStorageManager;

    public ThankYouMessageService(final ThankYouMessageRepository thankYouMessageRepository,
                                  final CommentRepository commentRepository,
                                  final ObjectStorageManager objectStorageManager) {
        this.thankYouMessageRepository = thankYouMessageRepository;
        this.commentRepository = commentRepository;
        this.objectStorageManager = objectStorageManager;
    }

    public ThankYouMessagesResponse findThankYouMessages(final long memberId, final long pageSize, final Long cursor) {
       return findPage(memberId, pageSize, cursor, thankYouMessageRepository);
    }

    @Override
    protected ThankYouMessagesResponse createResponse(final Long newCursor, final List<ThankYouMessage> items) {
        final List<ThankYouMessageResponse> responses = items.stream()
                .map(message -> ThankYouMessageResponse.of(
                        message,
                        commentRepository.countByThankYouMessageId(message.getId()),
                        objectStorageManager.createGetUrl(message.getWriterMember()
                                .getProfileImageName())
                )).collect(Collectors.toList());
        return new ThankYouMessagesResponse(newCursor, responses);
    }

    public SaveThankYouMessageResult saveThankYouMessage(final long writerMemberId, final long recipientId,
                                                         final String content) {
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(Member.createForJPA(writerMemberId))
                .recipientId(recipientId)
                .content(content)
                .build();
        thankYouMessageRepository.save(thankYouMessage);
        return new SaveThankYouMessageResult(writerMemberId, recipientId);
    }

    public void deleteThankYouMessage(final long thankYouMessageId, final long memberId) {
        if (thankYouMessageRepository.existsByIdAndWriterMemberId(thankYouMessageId, memberId)) {
            thankYouMessageRepository.deleteById(thankYouMessageId);
            return;
        }
        throw new IllegalArgumentException(NO_EXIST_THANK_YOU_MESSAGE);
    }
}
