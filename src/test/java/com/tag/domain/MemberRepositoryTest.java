package com.tag.domain;

import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 이메일로_회원을_조회한다() {
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
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
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
        final Member member = Member.builder()
                .email("test@test.com")
                .isRegistered(true)
                .build();
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
        // when
        final Optional<Boolean> registered = memberRepository.isRegistered(100L);

        // then
        Assertions.assertThat(registered).isEmpty();
    }

    @Test
    void 회원_아이디로_이메일을_조회한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        final long memberId = memberRepository.save(member)
                .getId();

        // when
        final String email = memberRepository.findEmailById(memberId)
                .get();

        // then
        Assertions.assertThat(email).isEqualTo("test@test.com");
    }

    @Test
    void 회원_아이디로_이메일을_조회한다_존재하지_않는_경우() {
        // when
        final Optional<String> email = memberRepository.findEmailById(999L);

        // then
        Assertions.assertThat(email).isEmpty();
    }
}
