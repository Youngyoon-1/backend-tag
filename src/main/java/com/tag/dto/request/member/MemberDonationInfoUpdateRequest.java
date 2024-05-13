package com.tag.dto.request.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberDonationInfoUpdateRequest {

    @Size(min = 2, max = 10)
    private String bankName;
    @Size(min = 9, max = 15)
    private String accountNumber;
    @Size(min = 2, max = 15)
    private String accountHolder;
    @Size(min = 10, max = 100)
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
