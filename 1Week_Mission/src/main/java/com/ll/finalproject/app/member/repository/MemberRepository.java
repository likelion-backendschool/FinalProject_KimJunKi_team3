package com.ll.finalproject.app.member.repository;

import com.ll.finalproject.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username);

    Optional<Member> findByUsername(String username);
}
