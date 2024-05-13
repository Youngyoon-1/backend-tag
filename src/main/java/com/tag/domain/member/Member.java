package com.tag.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "members")
@Getter
@Builder
@AllArgsConstructor
@ToString
public final class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memberSeq")
    @SequenceGenerator(name = "memberSeq", sequenceName = "MEMBERS_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "is_registered")
    private boolean isRegistered;

    @Column(name = "is_confirmed_mail_notification")
    private boolean isConfirmedMailNotification;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "introduction", length = 500)
    private String introduction;

    @Column(name = "profile_image_name", length = 100)
    private String profileImageName;

    @Column(name = "bank_name", length = 20)
    private String bankName;

    @Column(name = "account_number", length = 100)
    @Convert(converter = AccountNumberConverter.class)
    private String accountNumber;

    @Column(name = "account_holder", length = 15)
    private String accountHolder;

    @Column(name = "remit_link", length = 100)
    private String remitLink;

    protected Member() {
    }

    public Member(final long id) {
        this.id = id;
    }

    public static Member createForSave(final String email) {
        return Member.builder()
                .email(email)
                .build();
    }

    public static Member createForJPA(final long memberId) {
        return Member.builder()
                .id(memberId)
                .build();
    }

    public void updateMail(final String email) {
        this.email = email;
    }

    public void updateProfileImageName(final String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public void updateIntroduction(final String introduction) {
        this.introduction = introduction;
    }

    public void register() {
        this.isRegistered = true;
    }

    public void updateIsConfirmedMailNotification(final boolean isConfirmed) {
        this.isConfirmedMailNotification = isConfirmed;
    }

    public void updateDonationInfo(final String bankName, final String accountNumber, final String accountHolder,
                                   final String remitLink) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.remitLink = remitLink;
    }

    public DonationInfo getDonationInfo() {
        return new DonationInfo(bankName, accountNumber, accountHolder, remitLink);
    }
}
