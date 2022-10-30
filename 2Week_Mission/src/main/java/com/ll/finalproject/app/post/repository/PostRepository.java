package com.ll.finalproject.app.post.repository;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findFirst100ByOrderByIdDesc();

    List<Post> findByAuthor(Member member);

    List<Post> findByAuthorId(Long memberId);

    List<Post> findAllByOrderByIdDesc();

}
