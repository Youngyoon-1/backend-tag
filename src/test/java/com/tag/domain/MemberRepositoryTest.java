package com.tag.domain;

import com.tag.acceptance.WithTestcontainers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@JpaTest
public class MemberRepositoryTest extends WithTestcontainers {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_정보를_저장한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();

        // when
        final Member savedMember = memberRepository.save(member);

        // then
        final Long id = savedMember.getId();
        Assertions.assertThat(id).isNotNull();
    }

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
}
