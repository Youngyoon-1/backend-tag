package com.tag.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GoogleProfileResponseTest {

    @Test
    void 구글_프로필_응답을_멤버_엔티티로_변환한다() {
        // given
        final GoogleProfileResponse googleProfileResponse = new GoogleProfileResponse("test@test.com");

        // when
        final Member member = googleProfileResponse.toMember();

        // then
        final String email = member.getEmail();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com")
        );
    }
}
