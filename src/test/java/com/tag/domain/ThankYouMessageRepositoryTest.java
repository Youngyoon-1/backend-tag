package com.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.domain.thankYouMessage.ThankYouMessage;
import com.tag.domain.thankYouMessage.ThankYouMessageRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaTest
class ThankYouMessageRepositoryTest {

    @Autowired
    private ThankYouMessageRepository thankYouMessageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 감사_메세지를_조회한다_조회_시작_기준이_되는_감사메세지_아이디가_주어진_경우() {
        // given
        final Member member = saveMember();
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .writerMember(member)
                .recipientId(10L)
                .content("content1")
                .build();
        thankYouMessageRepository.save(thankYouMessage1);
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .writerMember(member)
                .recipientId(10L)
                .content("content2")
                .build();
        thankYouMessageRepository.save(thankYouMessage2);

        // when
        final Long fromId = thankYouMessage2.getId();
        final List<ThankYouMessage> thankYouMessages = thankYouMessageRepository.findPage(10L, 2L, fromId);

        // then
        final int size = thankYouMessages.size();
        final ThankYouMessage actualThankYouMessage = thankYouMessages.get(0);
        Assertions.assertAll(
                () -> assertThat(size).isOne(),
                () -> assertThat(actualThankYouMessage).usingRecursiveComparison()
                        .isEqualTo(thankYouMessage1)
        );
    }

    private Member saveMember() {
        return memberRepository.save(Member.builder().email("test@test.com").build());
    }

    @Test
    void 감사_메세지를_조회한다_조회_시작_기준이_되는_감사메세지_아이디가_null_인_경우() {
        // given
        final Member member = saveMember();
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member)
                .content("content1")
                .build();
        thankYouMessageRepository.save(thankYouMessage1);
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member)
                .content("content2")
                .build();
        thankYouMessageRepository.save(thankYouMessage2);

        // when
        final List<ThankYouMessage> thankYouMessages = thankYouMessageRepository.findPage(10L, 2L, null);

        // then
        final int size = thankYouMessages.size();
        final ThankYouMessage actualThankYouMessage1 = thankYouMessages.get(0);
        final ThankYouMessage actualThankYouMessage2 = thankYouMessages.get(1);
        Assertions.assertAll(
                () -> assertThat(size).isEqualTo(2L),
                () -> assertThat(actualThankYouMessage1).usingRecursiveComparison()
                        .isEqualTo(thankYouMessage2),
                () -> assertThat(actualThankYouMessage2).usingRecursiveComparison()
                        .isEqualTo(thankYouMessage1)
        );
    }

    @Test
    void 감사메세지_아이디와_회원_아이디로_감사메세지의_존재_여부를_확인할_수_있다() {
        // given
        final Member member = saveMember();
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(member)
                .recipientId(10L)
                .content("content")
                .build();
        final long thankYouMessageId = thankYouMessageRepository.save(thankYouMessage)
                .getId();

        // when
        final boolean exists = thankYouMessageRepository.existsByIdAndWriterMemberId(thankYouMessageId, member.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 감사메세지_아이디와_회원_아이디로_감사메세지의_존재_여부를_확인할_수_있다_감사메세지가_존재하지_않는_경우() {
        // given
        final long memberId = saveMember()
                .getId();

        // when
        final boolean exists = thankYouMessageRepository.existsByIdAndWriterMemberId(999L, memberId);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 감사메세지_아이디와_회원_아이디로_감사메세지의_존재_여부를_확인할_수_있다_회원_아이디가_존재하지_않을_경우() {
        // given
        final Member member = saveMember();
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(member)
                .recipientId(10L)
                .content("content")
                .build();
        final long thankYouMessageId = thankYouMessageRepository.save(thankYouMessage)
                .getId();

        // when
        final boolean exists = thankYouMessageRepository.existsByIdAndWriterMemberId(thankYouMessageId, 999L);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 감사메세지_아이디와_회원_아이디로_감사메세지의_존재_여부를_확인할_수_있다_모두_존재하지_않을_경우() {
        // when
        final boolean exists = thankYouMessageRepository.existsByIdAndWriterMemberId(999L, 999L);

        // then
        assertThat(exists).isFalse();
    }
}
