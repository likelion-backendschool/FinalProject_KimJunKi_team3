package com.ll.exam.final__2022_10_08.app.myBook.repository;

import com.ll.exam.final__2022_10_08.app.myBook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook, Long> {
    void deleteByProductIdAndOwnerId(long productId, long ownerId);

    List<MyBook> findAllByOwnerId(long ownerId);

    boolean existsByIdAndOwnerId(Long myBookId, Long memberId);
}
