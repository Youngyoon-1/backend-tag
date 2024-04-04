package com.tag.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void 회원정보를_수정한다() {
        // given
        final Member member1 = Member.builder()
                .email("test1@test.com")
                .build();
        final Member member2 = Member.builder()
                .email("test2@test.com")
                .build();

        // when
        member1.update(member2);

        // then
        Assertions.assertThat(member1).usingRecursiveComparison()
                .isEqualTo(member2);
    }
}
