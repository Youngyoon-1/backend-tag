package com.tag.dto.request;

import lombok.Getter;

@Getter
public class MemberInfoUpdateRequest {

    private String content;

    private MemberInfoUpdateRequest() {
    }

    public MemberInfoUpdateRequest(final String content) {
        this.content = content;
    }
}
