package com.tag.dto.request;

import lombok.Getter;

@Getter
public class MemberDonationInfoUpdateRequest {

    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String remitLink;

    private MemberDonationInfoUpdateRequest() {
    }

    public MemberDonationInfoUpdateRequest(final String bankName, final String accountNumber,
                                           final String accountHolder,
                                           final String remitLink) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.remitLink = remitLink;
    }
}
