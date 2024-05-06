package com.tag.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberInfoUpdateResponse {

    private String content;

    private MemberInfoUpdateResponse() {
    }

    public MemberInfoUpdateResponse(final String content) {
        this.content = content;
    }
}
