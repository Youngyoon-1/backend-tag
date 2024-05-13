package com.tag.domain.thankYouMessage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThankYouMessageRepository extends JpaRepository<ThankYouMessage, Long>,
        ThankYouMessageRepositoryCustom {

    boolean existsByIdAndWriterMemberId(long thankYouMessageId, long memberId);
}
