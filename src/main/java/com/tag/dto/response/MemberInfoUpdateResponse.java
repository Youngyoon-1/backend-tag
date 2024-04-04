package com.tag.dto.response;

import lombok.Getter;

@Getter
public class MemberInfoUpdateResponse {

    private String content;

    private MemberInfoUpdateResponse() {
    }

    public MemberInfoUpdateResponse(final String content) {
        this.content = content;
    }
}
