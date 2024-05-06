package com.tag.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ThankYouMessageRequest {

    private String content;

    private ThankYouMessageRequest() {
    }

    public ThankYouMessageRequest(final String content) {
        this.content = content;
    }
}
