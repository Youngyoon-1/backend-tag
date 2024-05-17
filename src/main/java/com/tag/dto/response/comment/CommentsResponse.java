package com.tag.dto.response.comment;

import java.util.List;

public record CommentsResponse(Long cursor, List<CommentResponse> commentResponses) {
}
