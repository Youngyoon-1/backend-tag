package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.application.image.ObjectStorageManager;
import com.tag.application.thankYouMessage.ThankYouMessageService;
import com.tag.domain.comment.CommentRepository;
import com.tag.domain.member.Member;
import com.tag.domain.thankYouMessage.ThankYouMessage;
import com.tag.domain.thankYouMessage.ThankYouMessageRepository;
import com.tag.dto.response.thankYouMessage.SaveThankYouMessageResult;
import com.tag.dto.response.thankYouMessage.ThankYouMessageMemberResponse;
import com.tag.dto.response.thankYouMessage.ThankYouMessageResponse;
import com.tag.dto.response.thankYouMessage.ThankYouMessagesResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ThankYouMessageServiceTest {

    @Mock
    private ThankYouMessageRepository thankYouMessageRepository;

    @InjectMocks
    private ThankYouMessageService thankYouMessageService;

    @Mock
    private ObjectStorageManager objectStorageManager;

    @Mock
    private CommentRepository commentRepository;

    @Test
    void 감사_메세지_페이지를_조회한다_마지막_페이지인_경우() {
        // given
        final Member member = Member.builder()
                .id(2L)
                .email("test@test.com")
                .profileImageName("profileImageName")
                .build();
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .id(1L)
                .writerMember(member)
                .recipientId(3L)
                .content("thankYouMessageContent")
                .build();
        BDDMockito.given(thankYouMessageRepository.findPage(2L, 2L, null))
                .willReturn(List.of(thankYouMessage));
        BDDMockito.given(commentRepository.countByThankYouMessageId(1L))
                .willReturn(0L);
        BDDMockito.given(objectStorageManager.createGetUrl("profileImageName"))
                .willReturn("profileImageUrl");

        // when
        final ThankYouMessagesResponse thankYouMessagesResponse = thankYouMessageService.findThankYouMessages(2L, 1L,
                null);

        // then
        final Long cursor = thankYouMessagesResponse.cursor();
        final ThankYouMessageResponse thankYouMessageResponse = thankYouMessagesResponse.thankYouMessageResponses()
                .get(0);
        final long thankYouMessageId = thankYouMessageResponse.id();
        final long commentCount = thankYouMessageResponse.commentCount();
        final String content = thankYouMessageResponse.content();
        final ThankYouMessageMemberResponse memberResponse = thankYouMessageResponse.memberResponse();
        final long memberId = memberResponse.id();
        final String profileUrl = memberResponse.profileUrl();
        final String email = memberResponse.email();
        Assertions.assertAll(
                () -> assertThat(cursor).isNull(),
                () -> assertThat(thankYouMessageId).isOne(),
                () -> assertThat(commentCount).isZero(),
                () -> assertThat(content).isEqualTo("thankYouMessageContent"),
                () -> assertThat(memberId).isEqualTo(2L),
                () -> assertThat(profileUrl).isEqualTo("profileImageUrl"),
                () -> assertThat(email).isEqualTo("test@test.com")
        );
    }

    @Test
    void 감사_메세지_페이지를_조회한다_마지막_페이지가_아닌_경우() {
        // given
        final Member member = Member.builder()
                .id(2L)
                .email("test@test.com")
                .profileImageName("profileImageName")
                .build();
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .id(1L)
                .writerMember(member)
                .recipientId(3L)
                .content("thankYouMessageContent1")
                .build();
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .id(2L)
                .writerMember(member)
                .recipientId(3L)
                .content("thankYouMessageContent2")
                .build();
        final ArrayList<ThankYouMessage> thankYouMessages = new ArrayList<>();
        thankYouMessages.add(thankYouMessage2);
        thankYouMessages.add(thankYouMessage1);
        BDDMockito.given(thankYouMessageRepository.findPage(2L, 2L, null))
                .willReturn(thankYouMessages);
        BDDMockito.given(commentRepository.countByThankYouMessageId(2L))
                .willReturn(0L);
        BDDMockito.given(objectStorageManager.createGetUrl("profileImageName"))
                .willReturn("profileImageUrl");

        // when
        final ThankYouMessagesResponse thankYouMessagesResponse = thankYouMessageService.findThankYouMessages(2L, 1L,
                null);

        // then
        final long cursor = thankYouMessagesResponse.cursor();
        final ThankYouMessageResponse thankYouMessageResponse = thankYouMessagesResponse.thankYouMessageResponses()
                .get(0);
        final long thankYouMessageId = thankYouMessageResponse.id();
        final long commentCount = thankYouMessageResponse.commentCount();
        final String content = thankYouMessageResponse.content();
        final ThankYouMessageMemberResponse memberResponse = thankYouMessageResponse.memberResponse();
        final long memberId = memberResponse.id();
        final String profileUrl = memberResponse.profileUrl();
        final String email = memberResponse.email();
        Assertions.assertAll(
                () -> assertThat(cursor).isEqualTo(2L),
                () -> assertThat(thankYouMessageId).isEqualTo(2L),
                () -> assertThat(commentCount).isZero(),
                () -> assertThat(content).isEqualTo("thankYouMessageContent2"),
                () -> assertThat(memberId).isEqualTo(2L),
                () -> assertThat(profileUrl).isEqualTo("profileImageUrl"),
                () -> assertThat(email).isEqualTo("test@test.com")
        );
    }

    @Test
    void 감사_메세지_페이지를_조회한다_페이지_사이즈가_유효하지_않을_경우_예외가_발생한다() {
        assertThatThrownBy(
                () -> thankYouMessageService.findThankYouMessages(1L, 21, null)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("페이지 사이즈가 20 을 초과할 수 없습니다.");
    }

    @Test
    void 감사_메세지를_저장한다() {
        // when
        final SaveThankYouMessageResult result = thankYouMessageService.saveThankYouMessage(10L, 1L, "content");

        // then
        final long writerMemberId = result.writerMemberId();
        final long recipientId = result.recipientId();
        Assertions.assertAll(
                () -> assertThat(writerMemberId).isEqualTo(10L),
                () -> assertThat(recipientId).isOne(),
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .save(BDDMockito.any(ThankYouMessage.class))
        );
    }

    @Test
    void 감사_메세지를_삭제한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsByIdAndWriterMemberId(1L, 10L))
                .willReturn(true);

        // when
        thankYouMessageService.deleteThankYouMessage(1L, 10L);

        // then
        BDDMockito.verify(thankYouMessageRepository)
                .deleteById(1L);
    }

    @Test
    void 감사_메세지를_삭제한다_자신이_작성한_감사메세지가_아니면_예외가_발생한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsByIdAndWriterMemberId(1L, 10L))
                .willReturn(false);

        // when, then
        assertThatThrownBy(
                () -> thankYouMessageService.deleteThankYouMessage(1L, 10L)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("감사메세지 아이디가 존재하지 않아 삭제에 실패했습니다.");
    }
}
