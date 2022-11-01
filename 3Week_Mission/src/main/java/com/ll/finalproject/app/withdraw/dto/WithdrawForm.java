package com.ll.finalproject.app.withdraw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WithdrawForm {
    @NotBlank(message = "은행명은 입력해주세요.")
    private String bankName;

    @NotNull(message = "계좌번호를 입력해주세요.")
    private Integer bankAccountNo;

    @NotNull(message = "출금 금액을 입력해주세요.")
    private Integer price;
}
