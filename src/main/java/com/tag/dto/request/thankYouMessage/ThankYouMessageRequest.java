package com.tag.dto.request.thankYouMessage;

import jakarta.validation.constraints.Size;

public record ThankYouMessageRequest(@Size(min = 1, max = 400) String content) {
}
