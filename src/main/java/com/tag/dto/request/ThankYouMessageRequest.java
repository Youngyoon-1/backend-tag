package com.tag.dto.request;

import lombok.Getter;

@Getter
public class ThankYouMessageRequest {

    private String content;

    private ThankYouMessageRequest() {
    }

    public ThankYouMessageRequest(final String content) {
        this.content = content;
    }
}
