package com.tag.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberImageUploadUrlResponse {

    private String url;
    private String imageName;

    private MemberImageUploadUrlResponse() {
    }

    public MemberImageUploadUrlResponse(final String url, final String imageName) {
        this.url = url;
        this.imageName = imageName;
    }
}
