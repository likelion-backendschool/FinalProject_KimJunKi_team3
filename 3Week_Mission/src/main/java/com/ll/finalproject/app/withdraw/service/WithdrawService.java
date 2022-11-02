package com.ll.finalproject.app.withdraw.service;

import com.ll.finalproject.app.base.rq.Rq;
import com.ll.finalproject.app.cash.entity.CashLog;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.exception.MemberNotFoundException;
import com.ll.finalproject.app.member.repository.MemberRepository;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.withdraw.entity.Withdraw;
import com.ll.finalproject.app.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final WithdrawRepository withdrawRepository;

    private final Rq rq;

    /**
     * 출금 신청
     *  1. 해당 유저가 맞는지 체크
     *  2. 출금 금액 유효성 체크
     *  3. 예치금 갱신
     *  4. db에 출금내역 저장
     */
    public void apply(Long memberId, String bankName, int bankAccountNo, long price) {

        Member member = getMember(memberId);

        if (price > member.getRestCash()) {
            throw new RuntimeException("출금 금액 초과");
        }

        CashLog cashLog = memberService.addCash(member, price * -1, "출금신청__예치금").getData().getCashLog();

        Withdraw withdraw = Withdraw
                .builder()
                .member(member)
                .bankName(bankName)
                .bankAccountNo(bankAccountNo)
                .price(price)
                .withdrawCashLog(cashLog)
                .build();

        withdrawRepository.save(withdraw);
    }

    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }
    @Transactional(readOnly = true)
    public Withdraw getWithdraw(Long withdrawId) {
        return withdrawRepository.findById(withdrawId).orElseThrow(
                () -> new RuntimeException(""));
    }

    public List<Withdraw> getWithDraw() {
        return withdrawRepository.findAllByOrderByIdDesc();
    }

    public void process(long withdrawId) {
        Withdraw withdraw = getWithdraw(withdrawId);

        CashLog cashLog = memberService.addCash(withdraw.getMember(), 0, "출금완료__예치금").getData().getCashLog();

        /**
         * 출금 로직
         */

        withdraw.setWithdrawDone(cashLog.getId());

    }
}
