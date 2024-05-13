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
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Member member3 = createMember("test2");
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .writerMember(member1)
                .recipientId(10L)
                .content("thankYouMessage1Content")
                .build();
        thankYouMessageRepository.save(thankYouMessage1);
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .writerMember(member2)
                .recipientId(10L)
                .content("thankYouMessage2Content")
                .build();
        thankYouMessageRepository.save(thankYouMessage2);
        final ThankYouMessage thankYouMessage3 = ThankYouMessage.builder()
                .writerMember(member3)
                .recipientId(10L)
                .content("thankYouMessage3Content")
                .build();
        thankYouMessageRepository.save(thankYouMessage3);

        // when
        final Long fromId = thankYouMessage2.getId();
        final List<ThankYouMessage> thankYouMessages = thankYouMessageRepository.findPage(10L, 2L, fromId);

        // then
        final int size = thankYouMessages.size();
        final ThankYouMessage actualThankYouMessage1 = thankYouMessages.get(0);
        Assertions.assertAll(
                () -> assertThat(size).isEqualTo(1L),
                () -> assertThat(actualThankYouMessage1).usingRecursiveComparison()
                        .isEqualTo(thankYouMessage1)
        );
    }

    private Member createMember(final String email) {
        return memberRepository.save(Member.builder().email(email).build());
    }

    @Test
    void 감사_메세지를_조회한다_조회_시작_기준이_되는_감사메세지_아이디가_없는_경우() {
        // given
        final Member member1 = createMember("test1");
        final Member member2 = createMember("test2");
        final Member member3 = createMember("test2");
        final ThankYouMessage thankYouMessage1 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member1)
                .content("thankYouMessage1Content")
                .build();
        thankYouMessageRepository.save(thankYouMessage1);
        final ThankYouMessage thankYouMessage2 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member2)
                .content("thankYouMessage2Content")
                .build();
        thankYouMessageRepository.save(thankYouMessage2);
        final ThankYouMessage thankYouMessage3 = ThankYouMessage.builder()
                .recipientId(10L)
                .writerMember(member3)
                .content("thankYouMessage3Content")
                .build();
        thankYouMessageRepository.save(thankYouMessage3);

        // when
        final List<ThankYouMessage> thankYouMessages = thankYouMessageRepository.findPage(10L, 2L, null);

        // then
        final int size = thankYouMessages.size();
        final ThankYouMessage actualThankYouMessage1 = thankYouMessages.get(0);
        final ThankYouMessage actualThankYouMessage2 = thankYouMessages.get(1);
        Assertions.assertAll(
                () -> assertThat(size).isEqualTo(2L),
                () -> assertThat(actualThankYouMessage1).usingRecursiveComparison()
                        .isEqualTo(thankYouMessage3),
                () -> assertThat(actualThankYouMessage2).usingRecursiveComparison()
                        .isEqualTo(thankYouMessage2)
        );
    }

    @Test
    void 감사메세지_아이디와_회원_아이디로_감사메세지의_존재_여부를_확인할_수_있다_존재하는_경우() {
        // given
        final Member member = createMember("test1");
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
    void 감사메세지_아이디와_회원_아이디로_감사메세지의_존재_여부를_확인할_수_있다_존재하지_않는_경우() {
        // given
        final Member member = createMember("test1");
        final ThankYouMessage thankYouMessage = ThankYouMessage.builder()
                .writerMember(member)
                .recipientId(10L)
                .content("content")
                .build();
        final long thankYouMessageId = thankYouMessageRepository.save(thankYouMessage)
                .getId();

        // when
        final boolean exists = thankYouMessageRepository.existsByIdAndWriterMemberId(thankYouMessageId, 1L);

        // then
        assertThat(exists).isFalse();
    }
}
