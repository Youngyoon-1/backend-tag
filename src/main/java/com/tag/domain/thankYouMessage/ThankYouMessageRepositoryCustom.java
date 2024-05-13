package com.tag.domain.thankYouMessage;

import java.util.List;

public interface ThankYouMessageRepositoryCustom {

    List<ThankYouMessage> findPage(final long memberId, final long pageSize, final Long fromId);
}
