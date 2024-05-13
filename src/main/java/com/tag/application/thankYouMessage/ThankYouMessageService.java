package com.tag.application.thankYouMessage;

import com.tag.application.image.ObjectStorageManager;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThankYouMessageService {

    private static final long THANK_YOU_MESSAGE_PAGE_SIZE_LIMIT = 20;
    private static final long ONE_CHECK_FOR_LAST_PAGE = 1;
    private static final int ONE_FOR_NEW_CURSOR = 1;

    private static final String INVALID_THANK_YOU_MESSAGE_PAGE_SIZE = "페이지 사이즈가 20 을 초과할 수 없습니다.";
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

    @Transactional(readOnly = true)
    public ThankYouMessagesResponse findThankYouMessages(final long memberId, final long pageSize, final Long cursor) {
        if (pageSize > THANK_YOU_MESSAGE_PAGE_SIZE_LIMIT) {
            throw new IllegalArgumentException(INVALID_THANK_YOU_MESSAGE_PAGE_SIZE);
        }
        final long pageSizeForCheckLastPage = pageSize + ONE_CHECK_FOR_LAST_PAGE;
        final List<ThankYouMessage> thankYouMessages = thankYouMessageRepository.findPage(memberId,
                pageSizeForCheckLastPage, cursor);
        final int selectedSize = thankYouMessages.size();
        if (selectedSize == pageSizeForCheckLastPage) {
            final int lastIndexForRemove = (int) pageSize;
            thankYouMessages.remove(lastIndexForRemove);
            final int lastIndexForNewCursor = lastIndexForRemove - ONE_FOR_NEW_CURSOR;
            final long newCursor = thankYouMessages.get(lastIndexForNewCursor)
                    .getId();
            final List<ThankYouMessageResponse> thankYouMessageResponses = getThankYouMessageResponses(
                    thankYouMessages);
            return new ThankYouMessagesResponse(newCursor, thankYouMessageResponses);
        }
        final List<ThankYouMessageResponse> thankYouMessageResponses = getThankYouMessageResponses(
                thankYouMessages);
        return new ThankYouMessagesResponse(null, thankYouMessageResponses);
    }

    private List<ThankYouMessageResponse> getThankYouMessageResponses(final List<ThankYouMessage> thankYouMessages) {
        return thankYouMessages.stream()
                .map(message -> ThankYouMessageResponse.of(
                        message,
                        commentRepository.countByThankYouMessageId(message.getId()),
                        objectStorageManager.createGetUrl(message.getWriterMember()
                                .getProfileImageName())
                )).collect(Collectors.toList());
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
