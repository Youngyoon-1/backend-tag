package com.tag.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class DonationInfo {

    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private String remitLink;
}
