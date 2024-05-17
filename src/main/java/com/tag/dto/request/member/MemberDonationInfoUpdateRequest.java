package com.tag.dto.request.member;

import jakarta.validation.constraints.Size;

/**
 * TODO: 검증로직 추가
 *
 * @param bankName      @Size(min = 2, max = 10)
 * @param accountNumber @Size(min = 9, max = 15)
 * @param accountHolder @Size(min = 2, max = 15)
 * @param remitLink     @Size(min = 10, max = 100)
 */
public record MemberDonationInfoUpdateRequest(@Size(max = 10) String bankName, @Size(max = 15) String accountNumber,
                                              @Size(max = 15) String accountHolder, @Size(max = 100) String remitLink) {
}
