package com.tag.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberImageNameUpdateRequest {

    private String imageName;

    private MemberImageNameUpdateRequest() {
    }

    public MemberImageNameUpdateRequest(final String imageName) {
        this.imageName = imageName;
    }
}
