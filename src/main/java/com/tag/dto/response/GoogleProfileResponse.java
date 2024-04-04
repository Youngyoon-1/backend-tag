package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.Getter;

@Getter
public class GoogleProfileResponse {

    private String email;

    private GoogleProfileResponse() {
    }

    public GoogleProfileResponse(final String email) {
        this.email = email;
    }

    public Member toMember() {
        return Member.builder()
                .email(email)
                .build();
    }
}
