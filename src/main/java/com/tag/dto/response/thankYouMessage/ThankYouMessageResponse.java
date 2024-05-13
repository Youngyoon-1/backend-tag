package com.tag.dto.response.thankYouMessage;

import com.tag.domain.thankYouMessage.ThankYouMessage;
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
