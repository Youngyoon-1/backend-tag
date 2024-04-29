package com.tag.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Comment> findPage(final Long thankYouMessageId, final Long pageSize, final Long cursor) {
        return jpaQueryFactory.selectFrom(QComment.comment)
                .where(
                        ltCommentId(cursor),
                        QComment.comment
                                .thankYouMessageId
                                .eq(thankYouMessageId))
                .orderBy(QComment.comment
                        .id
                        .desc())
                .limit(pageSize)
                .leftJoin(QComment.comment.member)
                .fetchJoin()
                .fetch();
    }

    private BooleanExpression ltCommentId(final Long commentId) {
        if (commentId == null) {
            return null;
        }
        return QComment.comment
                .id
                .lt(commentId);
    }
}
