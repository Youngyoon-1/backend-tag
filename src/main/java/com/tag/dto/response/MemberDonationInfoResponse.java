package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.Getter;

@Getter
public class MemberDonationInfoResponse {

    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String remitLink;

    private MemberDonationInfoResponse() {
    }

    public MemberDonationInfoResponse(final Member member) {
        this.bankName = member.getBankName();
        this.accountNumber = member.getAccountNumber();
        this.accountHolder = member.getAccountHolder();
        this.remitLink = member.getRemitLink();
    }
}
