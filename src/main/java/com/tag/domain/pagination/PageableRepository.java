package com.tag.domain.pagination;

import java.util.List;

public interface PageableRepository<T extends Identifiable> {
    List<T> findPage(long id, long pageSize, Long cursor);
}
