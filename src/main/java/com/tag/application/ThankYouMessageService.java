package com.tag.application;

import com.tag.domain.CommentRepository;
import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.domain.ThankYouMessage;
import com.tag.domain.ThankYouMessageRepository;
import com.tag.dto.response.SaveThankYouMessageResult;
import com.tag.dto.response.ThankYouMessageResponse;
import com.tag.dto.response.ThankYouMessagesResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ThankYouMessageService {

    private final ThankYouMessageRepository thankYouMessageRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final ObjectStorageManager objectStorageManager;
    private final SendMailService sendMailService;

    public ThankYouMessageService(final ThankYouMessageRepository thankYouMessageRepository,
                                  final MemberRepository memberRepository,
                                  final CommentRepository commentRepository,
                                  final ObjectStorageManager objectStorageManager,
                                  final SendMailService sendMailService) {
        this.thankYouMessageRepository = thankYouMessageRepository;
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
        this.objectStorageManager = objectStorageManager;
        this.sendMailService = sendMailService;
    }

    public ThankYouMessagesResponse findThankYouMessages(final Long memberId, final Long pageSize, final Long cursor) {
        // validate pageSize
        if (pageSize > 20) {
            throw new RuntimeException("유효하지 않은 pageSize 값입니다.");
        }

        // 한 개를 추가로 더 조회해서 마지막 페이지인지 알 수 있도록 한다
        final List<ThankYouMessage> thankYouMessages = thankYouMessageRepository.findPage(memberId,
                pageSize + 1, cursor);
        final int selectedSize = thankYouMessages.size();

        // 다음 페이지 존재함
        if (selectedSize == pageSize + 1) {
            thankYouMessages.remove(selectedSize - 1);
            final Long newCursor = thankYouMessages.get(selectedSize - 2)
                    .getId();
            final List<ThankYouMessageResponse> thankYouMessageResponses = getThankYouMessageResponses(
                    thankYouMessages);
            return new ThankYouMessagesResponse(newCursor, thankYouMessageResponses);
        }

        // 다음 페이지 없음
        final List<ThankYouMessageResponse> thankYouMessageResponses = getThankYouMessageResponses(
                thankYouMessages);

        return new ThankYouMessagesResponse(null, thankYouMessageResponses);
    }

    private List<ThankYouMessageResponse> getThankYouMessageResponses(final List<ThankYouMessage> thankYouMessages) {
        return thankYouMessages.stream()
                .map(it -> ThankYouMessageResponse.from(it, commentRepository.countByThankYouMessageId(it.getId()),
                        getUrl(it.getWriterMember().getProfileImageName())))
                .collect(Collectors.toList());
    }

    private String getUrl(final String imageName) {
        if (imageName != null) {
            return objectStorageManager.createPresignedGetUrl(imageName,
                    MemberImageCategory.PROFILE);
        }
        return null;
    }

    //    @Transactional
//    public SaveThankYouMessageResult saveThankYouMessage(final Long writerMemberId, final Long recipientId, final String content) {
//        if (content.length() > 400) {
//            throw new RuntimeException("감사메세지의 길이는 400자 이하여야 합니다.");
//        }
//        if (!memberRepository.existsById(recipientId)) {
//            throw new RuntimeException("존재하지 않는 회원입니다.");
//        }
//        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
//                .writerMember(new Member(writerMemberId))
//                .recipientId(recipientId)
//                .content(content)
//                .build();
//        thankYouMessageRepository.save(thankYouMessage);
//
//        return new SaveThankYouMessageResult(writerMemberId, recipientId);
//    }
    @Transactional
    public void saveThankYouMessage(final Long writerMemberId, final Long recipientId,
                                                         final String content) {
        if (content.length() > 400) {
            throw new RuntimeException("감사메세지의 길이는 400자 이하여야 합니다.");
        }
        if (!memberRepository.existsById(recipientId)) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(new Member(writerMemberId))
                .recipientId(recipientId)
                .content(content)
                .build();
        thankYouMessageRepository.save(thankYouMessage);
        sendMailService.sendMail(new SaveThankYouMessageResult(writerMemberId, recipientId));
    }

    @Transactional
    public void deleteThankYouMessage(final Long thankYouMessageId, final Long memberId) {
        if (thankYouMessageRepository.existsByIdAndWriterMemberId(thankYouMessageId, memberId)) {
            thankYouMessageRepository.deleteById(thankYouMessageId);
            commentRepository.deleteByThankYouMessageId(thankYouMessageId);
            return;
        }
        throw new RuntimeException("감사메세지 아이디가 유효하지 않습니다.");
    }
}
