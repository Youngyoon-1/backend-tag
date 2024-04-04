package com.tag.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m.isConfirmedMailNotification FROM Member m WHERE m.id = :memberId")
    Boolean IsConfirmedMailNotification(@Param("memberId") Long memberId);
}
