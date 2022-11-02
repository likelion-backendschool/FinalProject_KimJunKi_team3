package com.ll.finalproject.app.withdraw.entity;

import com.ll.finalproject.app.base.entity.BaseEntity;
import com.ll.finalproject.app.cash.entity.CashLog;
import com.ll.finalproject.app.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Withdraw extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private Member member;

    private String bankName;

    private int bankAccountNo;

    private long price;

    private boolean isWithdraw; // 출금여부

    private boolean isCanceled; // 취소여부

    private boolean isReject; // 거부여부

    private LocalDateTime withdrawDate; // 출금 날짜
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashLog withdrawCashLog; // 출금 내역


    public void setWithdrawDone(long cashLogId) {
        withdrawDate = LocalDateTime.now();
        isWithdraw = true;
        this.withdrawCashLog = new CashLog(cashLogId);
    }
}
