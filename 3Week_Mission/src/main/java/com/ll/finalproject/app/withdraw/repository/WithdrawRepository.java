package com.ll.finalproject.app.withdraw.repository;

import com.ll.finalproject.app.withdraw.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findAllByOrderByIdDesc();
}
