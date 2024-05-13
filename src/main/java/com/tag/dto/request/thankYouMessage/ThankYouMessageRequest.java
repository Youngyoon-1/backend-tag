package com.tag.dto.request.thankYouMessage;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ThankYouMessageRequest {

    @Size(min = 1, max = 400)
    private String content;

    private ThankYouMessageRequest() {
    }

    public ThankYouMessageRequest(final String content) {
        this.content = content;
    }
}
