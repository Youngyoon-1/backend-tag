package com.tag.dto.response;

import lombok.Getter;

@Getter
public class SaveThankYouMessageResult {

    private final Long writerMemberId;
    private final Long recipientId;

    public SaveThankYouMessageResult(final Long writerMemberId, final Long recipientId) {
        this.writerMemberId = writerMemberId;
        this.recipientId = recipientId;
    }
}
