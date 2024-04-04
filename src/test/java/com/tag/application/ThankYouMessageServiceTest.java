package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.domain.ThankYouMessage;
import com.tag.domain.ThankYouMessageRepository;
import com.tag.dto.response.ThankYouMessageResponse;
import com.tag.dto.response.ThankYouMessagesResponse;
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

    @Test
    void 감사_메세지_목록을_조회한다() {
        // given
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .id(1L)
                .memberId(10L)
                .content("thankYouMessageContent")
                .build();
        BDDMockito.given(thankYouMessageRepository.findPage(10L, 1L, 1L))
                .willReturn(List.of(thankYouMessage));

        // when
        final ThankYouMessagesResponse thankYouMessagesResponse = thankYouMessageService.findThankYouMessages(10L, 1L,
                1L);

        // then
        final ThankYouMessageResponse thankYouMessageResponse = thankYouMessagesResponse.getThankYouMessageResponses()
                .get(0);
        final long id = thankYouMessageResponse.getId();
        final long memberId = thankYouMessageResponse.getMemberId();
        final String content = thankYouMessageResponse.getContent();
        Assertions.assertAll(
                () -> assertThat(id).isOne(),
                () -> assertThat(memberId).isEqualTo(10L),
                () -> assertThat(content).isEqualTo("thankYouMessageContent"),
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .findPage(10L, 1L, 1L)
        );
    }

    @Test
    void 감사_메세지를_저장한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.save(BDDMockito.any(ThankYouMessage.class)))
                .willReturn(null);

        // when
        thankYouMessageService.saveThankYouMessage(10L, memberId, "content");

        // then
        BDDMockito.verify(thankYouMessageRepository)
                .save(BDDMockito.any(ThankYouMessage.class));
    }

    @Test
    void 감사_메세지를_삭제한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsByIdAndWriterMemberId(1L, 10L))
                .willReturn(Boolean.TRUE);
        BDDMockito.willDoNothing()
                .given(thankYouMessageRepository)
                .deleteById(1L);

        // when
        thankYouMessageService.deleteThankYouMessage(1L, 10L);

        // then
        Assertions.assertAll(
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .existsByIdAndWriterMemberId(1L, 10L),
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .deleteById(1L)
        );
    }

    @Test
    void 감사_메세지를_삭제한다_자신이_작성한_감사메세지가_아니면_예외가_발생한다() {
        // given
        BDDMockito.given(thankYouMessageRepository.existsByIdAndWriterMemberId(1L, 10L))
                .willReturn(Boolean.FALSE);

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> thankYouMessageService.deleteThankYouMessage(1L, 10L)
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("감사메세지 아이디가 유효하지 않습니다."),
                () -> BDDMockito.verify(thankYouMessageRepository)
                        .existsByIdAndWriterMemberId(1L, 10L)
        );
    }
}
