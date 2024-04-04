package com.tag.domain;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findPage(Long thankYouMessageId, Long pageSize, Long fromId);
}
