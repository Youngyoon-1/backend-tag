package com.tag.dto.response.thankYouMessage;

import java.util.List;

public record ThankYouMessagesResponse(Long cursor, List<ThankYouMessageResponse> thankYouMessageResponses) {
}
