package com.tag.domain;

import java.util.List;

public interface ThankYouMessageRepositoryCustom {

    List<ThankYouMessage> findPage(final long memberId, final Long pageSize, final Long fromId);
}
