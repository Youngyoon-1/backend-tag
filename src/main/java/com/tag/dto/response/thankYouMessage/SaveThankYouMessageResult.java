package com.tag.dto.response.thankYouMessage;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class SaveThankYouMessageResult {

    private final long writerMemberId;
    private final long recipientId;

    public SaveThankYouMessageResult(final long writerMemberId, final long recipientId) {
        this.writerMemberId = writerMemberId;
        this.recipientId = recipientId;
    }
}
