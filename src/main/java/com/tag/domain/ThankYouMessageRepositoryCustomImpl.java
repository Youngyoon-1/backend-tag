package com.tag.domain;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public final class ThankYouMessageRepositoryCustomImpl implements ThankYouMessageRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ThankYouMessageRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<ThankYouMessage> findPage(final long memberId, final Long pageSize, final Long cursor) {
        return jpaQueryFactory.selectFrom(QThankYouMessage.thankYouMessage)
                .where(
                        ltThankYouMessageId(cursor),
                        QThankYouMessage.thankYouMessage
                                .recipientId
                                .eq(memberId))
                .orderBy(QThankYouMessage.thankYouMessage
                        .id
                        .desc())
                .limit(pageSize)
                .leftJoin(QThankYouMessage.thankYouMessage.writerMember)
                .fetchJoin()
                .fetch();
    }

    private BooleanExpression ltThankYouMessageId(final Long thankYouMessageId) {
        if (thankYouMessageId == null) {
            return null;
        }
        return QThankYouMessage.thankYouMessage
                .id
                .lt(thankYouMessageId);
    }
}
