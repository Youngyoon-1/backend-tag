package com.tag.domain;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findPage(long thankYouMessageId, Long pageSize, Long cursor);
}
