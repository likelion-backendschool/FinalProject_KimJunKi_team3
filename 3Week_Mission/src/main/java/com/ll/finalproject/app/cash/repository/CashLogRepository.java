package com.ll.finalproject.app.cash.repository;

import com.ll.finalproject.app.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
