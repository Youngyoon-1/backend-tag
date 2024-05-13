package com.tag.domain.comment;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findPage(long thankYouMessageId, long pageSize, Long cursor);
}
