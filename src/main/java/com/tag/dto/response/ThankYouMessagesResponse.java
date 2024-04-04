package com.tag.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class ThankYouMessagesResponse {

    private Long cursor;
    private List<ThankYouMessageResponse> thankYouMessageResponses;

    private ThankYouMessagesResponse() {
    }

    public ThankYouMessagesResponse(final Long cursor, final List<ThankYouMessageResponse> thankYouMessageResponses) {
        this.cursor = cursor;
        this.thankYouMessageResponses = thankYouMessageResponses;
    }
}
