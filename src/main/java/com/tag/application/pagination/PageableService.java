package com.tag.application.pagination;

import com.tag.domain.pagination.Identifiable;
import com.tag.domain.pagination.PageableRepository;
import java.util.List;

public abstract class PageableService<T extends Identifiable, V> {

    private static final String INVALID_COMMENT_PAGE_SIZE = "페이지 사이즈가 20 을 초과할 수 없습니다.";

    private static final int COMMENT_PAGE_SIZE_LIMIT = 20;
    private static final int EXTRA_ITEM_FOR_CHECK = 1;
    private static final int ONE_FOR_NEW_CURSOR = 1;

    public V findPage(final long id, final long pageSize, final Long cursor,
                      final PageableRepository<T> pageableRepository) {
        if (pageSize > COMMENT_PAGE_SIZE_LIMIT) {
            throw new IllegalArgumentException(INVALID_COMMENT_PAGE_SIZE);
        }
        final long pageSizeForCheckLastPage = pageSize + EXTRA_ITEM_FOR_CHECK;
        final List<T> items = pageableRepository.findPage(id, pageSizeForCheckLastPage, cursor);
        final int selectedSize = items.size();
        if (selectedSize == pageSizeForCheckLastPage) {
            final int lastIndexForRemove = (int) pageSize;
            items.remove(lastIndexForRemove);
            final int lastIndexForNewCursor = lastIndexForRemove - ONE_FOR_NEW_CURSOR;
            final long newCursor = items.get(lastIndexForNewCursor)
                    .getId();
            return createResponse(newCursor, items);
        }
        return createResponse(null, items);
    }

    protected abstract V createResponse(final Long newCursor, final List<T> arguments);
}
