package com.tag.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    boolean existsByIdAndMemberId(long id, long memberId);

    long countByThankYouMessageId(long thankYouMessageId);

    void deleteByThankYouMessageId(long thankYouMessageId);
}
