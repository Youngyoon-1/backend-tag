package com.tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Boolean existsByIdAndMemberId(Long id, Long memberId);

    Long countByThankYouMessageId(Long thankYouMessageId);

    void deleteByThankYouMessageId(Long thankYouMessageId);
}
