package com.tag.dto.response.member;

import com.tag.domain.member.DonationInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public final class MemberResponse {

    private String email;
    private String introduction;
    private String profileImageUrl;
    private String profileImageName;
    private Boolean isConfirmedMailNotification;
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String remitLink;

    public MemberResponse() {
    }

    public void setDonationInfo(final DonationInfo donationInfo) {
        this.bankName = donationInfo.bankName();
        this.accountNumber = donationInfo.accountNumber();
        this.accountHolder = donationInfo.accountHolder();
        this.remitLink = donationInfo.remitLink();
    }
}
