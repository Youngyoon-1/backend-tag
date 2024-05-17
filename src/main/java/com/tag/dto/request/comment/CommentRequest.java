package com.tag.dto.request.comment;

import jakarta.validation.constraints.Size;

public record CommentRequest(@Size(min = 1, max = 400) String content) {
}
