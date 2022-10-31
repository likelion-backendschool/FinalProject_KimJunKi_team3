package com.ll.finalproject.app.post.repository;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findFirst100ByOrderByIdDesc();

    List<Post> findByAuthor(Member member);

    List<Post> findByAuthorId(Long memberId);

    List<Post> findAllByOrderByIdDesc();

    @Query("select p from PostHashTag ph inner join Post p on p = ph.post where ph.member.id = :memberId AND ph.postKeyword.id = :postKeywordId")
    List<Post> findAllByMemberIdAndPostKeyWordId(@Param("memberId") Long memberId, @Param("postKeywordId") Long postKeywordId);
}
