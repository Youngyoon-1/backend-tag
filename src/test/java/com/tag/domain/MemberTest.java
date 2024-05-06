package com.tag.domain;

import static org.junit.jupiter.api.Assertions.assertAll;

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
        member1.updateMail(member2.getEmail());

        // then
        Assertions.assertThat(member1).usingRecursiveComparison()
                .isEqualTo(member2);
    }

    @Test
    void 회원을_등록한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();

        // when
        member.register();

        // then
        Assertions.assertThat(member.isRegistered()).isTrue();
    }

    @Test
    void 후원정보를_업데이트한다() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();

        // when
        member.updateDonationInfo("테스트은행", "1234-1234-1234", "테스트", "https://test.com");

        // then
        final String bankName = member.getBankName();
        final String accountNumber = member.getAccountNumber();
        final String accountHolder = member.getAccountHolder();
        final String remitLink = member.getRemitLink();
        assertAll(
                () -> Assertions.assertThat(bankName).isEqualTo("테스트은행"),
                () -> Assertions.assertThat(accountNumber).isEqualTo("1234-1234-1234"),
                () -> Assertions.assertThat(accountHolder).isEqualTo("테스트"),
                () -> Assertions.assertThat(remitLink).isEqualTo("https://test.com")
        );
    }
}
