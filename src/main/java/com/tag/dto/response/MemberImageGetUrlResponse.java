package com.tag.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberImageGetUrlResponse {

    private String url;

    public MemberImageGetUrlResponse(final String url) {
        this.url = url;
    }
}
