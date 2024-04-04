package com.tag.dto.response;

import lombok.Getter;

@Getter
public class MemberImageGetUrlResponse {

    private String url;

    public MemberImageGetUrlResponse(final String url) {
        this.url = url;
    }
}
