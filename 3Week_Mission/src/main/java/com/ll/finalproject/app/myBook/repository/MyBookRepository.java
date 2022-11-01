package com.ll.finalproject.app.myBook.repository;

import com.ll.finalproject.app.myBook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyBookRepository extends JpaRepository<MyBook, Long> {

    void deleteByProductIdAndOwnerId(long productId, long ownerId);
}
