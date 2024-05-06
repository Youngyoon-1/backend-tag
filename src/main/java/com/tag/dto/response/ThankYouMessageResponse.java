package com.tag.dto.response;

import com.tag.domain.ThankYouMessage;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ThankYouMessageResponse {

    private long id;
    private ThankYouMessageMemberResponse memberResponse;
    private String content;
    private long commentCount;

    private ThankYouMessageResponse() {
    }

    public ThankYouMessageResponse(final long id, final ThankYouMessageMemberResponse memberResponse,
                                   final String content, final long commentCount) {
        this.id = id;
        this.memberResponse = memberResponse;
        this.content = content;
        this.commentCount = commentCount;
    }

    public static ThankYouMessageResponse from(final ThankYouMessage thankYouMessage, final long commentCount,
                                               final String profileUrl) {
        return new ThankYouMessageResponse(
                thankYouMessage.getId(),
                new ThankYouMessageMemberResponse(thankYouMessage.getWriterMember(), profileUrl),
                thankYouMessage.getContent(),
                commentCount
        );
    }
}
