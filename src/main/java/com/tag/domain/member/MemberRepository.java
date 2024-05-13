package com.tag.domain.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query("SELECT m.isRegistered FROM Member m WHERE m.id = :memberId")
    Optional<Boolean> isRegistered(@Param("memberId") long memberId);

    @Query("SELECT m.email FROM Member m WHERE m.id = :writerMemberId")
    Optional<String> findEmailById(@Param("writerMemberId") long memberId);
}
