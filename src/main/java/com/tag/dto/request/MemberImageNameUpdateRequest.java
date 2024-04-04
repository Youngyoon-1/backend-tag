package com.tag.dto.request;

import lombok.Getter;

@Getter
public class MemberImageNameUpdateRequest {

    private String imageName;

    private MemberImageNameUpdateRequest() {
    }

    public MemberImageNameUpdateRequest(final String imageName) {
        this.imageName = imageName;
    }
}
