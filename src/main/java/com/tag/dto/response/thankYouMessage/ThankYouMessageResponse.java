package com.tag.dto.response.thankYouMessage;

import com.tag.domain.thankYouMessage.ThankYouMessage;

public record ThankYouMessageResponse(long id, ThankYouMessageMemberResponse memberResponse, String content,
                                      long commentCount) {
    public static ThankYouMessageResponse of(final ThankYouMessage thankYouMessage, final long commentCount,
                                             final String profileUrl) {
        return new ThankYouMessageResponse(
                thankYouMessage.getId(),
                new ThankYouMessageMemberResponse(thankYouMessage.getWriterMember(), profileUrl),
                thankYouMessage.getContent(),
                commentCount
        );
    }
}
