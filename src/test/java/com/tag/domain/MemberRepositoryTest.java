package com.tag.domain;

import com.tag.acceptance.WithTestcontainers;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaTest
public class MemberRepositoryTest extends WithTestcontainers {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 구글_아이디로_회원을_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        memberRepository.save(member);

        // when
        final Member savedMember = memberRepository.findByEmail("test@test.com")
                .get();

        // then
        Assertions.assertThat(savedMember).usingRecursiveComparison()
                .isEqualTo(member);
    }

    @Test
    void 회원의_등록_여부를_조회한다_등록하지_않은_경우() {
        // given
        final Member member = Member.createForSave("test@test.com");
        final long memberId = memberRepository.save(member)
                .getId();

        // when
        final boolean registered = memberRepository.isRegistered(memberId)
                .get();

        // then
        Assertions.assertThat(registered).isFalse();
    }

    @Test
    void 회원의_등록_여부를_조회한다_등록한_경우() {
        // given
        final Member member = Member.createForSave("test@test.com");
        member.register();
        final long memberId = memberRepository.save(member)
                .getId();

        // when
        final boolean registered = memberRepository.isRegistered(memberId)
                .get();

        // then
        Assertions.assertThat(registered).isTrue();
    }

    @Test
    void 회원의_등록_여부를_조회한다_회원_아이디가_존재하지_않는_경우() {
        // given
        // when
        final Optional<Boolean> registered = memberRepository.isRegistered(100L);

        // then
        Assertions.assertThat(registered).isEmpty();
    }
}
