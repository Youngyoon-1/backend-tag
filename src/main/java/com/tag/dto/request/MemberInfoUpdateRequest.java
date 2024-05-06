package com.tag.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberInfoUpdateRequest {

    private String content;

    private MemberInfoUpdateRequest() {
    }

    public MemberInfoUpdateRequest(final String content) {
        this.content = content;
    }
}
