package com.tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThankYouMessageRepository extends JpaRepository<ThankYouMessage, Long>,
        ThankYouMessageRepositoryCustom {

    boolean existsByIdAndWriterMemberId(Long thankYouMessageId, Long memberId);
}
