package com.tag.domain;

import java.util.List;

public interface ThankYouMessageRepositoryCustom {

    List<ThankYouMessage> findPage(final Long memberId, final Long pageSize, final Long fromId);
}
