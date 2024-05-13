package com.tag.dto.response.thankYouMessage;

import com.tag.dto.response.thankYouMessage.ThankYouMessageResponse;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ThankYouMessagesResponse {

    private Long cursor;
    private List<ThankYouMessageResponse> thankYouMessageResponses;

    private ThankYouMessagesResponse() {
    }

    public ThankYouMessagesResponse(final Long cursor, final List<ThankYouMessageResponse> thankYouMessageResponses) {
        this.cursor = cursor;
        this.thankYouMessageResponses = thankYouMessageResponses;
    }
}
