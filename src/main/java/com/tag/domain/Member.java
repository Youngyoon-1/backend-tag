package com.tag.domain;

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

@Entity
@Table(name = "members")
@Getter
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "memberSeq")
    @SequenceGenerator(name = "memberSeq", sequenceName = "MEMBERS_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "is_registered")
    private boolean isRegistered;

    @Column(name = "is_confirmed_mail_notification")
    private boolean isConfirmedMailNotification;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "introductory_Article", length = 500)
    private String introductoryArticle;

    @Column(name = "profile_image_name", length = 100)
    private String profileImageName;

    @Column(name = "qr_image_name", length = 100)
    private String qrImageName;

    @Column(name = "qr_link_url", length = 100)
    private String qrLinkUrl;

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

    public Member(final Long id) {
        this.id = id;
    }

    public void update(final Member member) {
        this.email = member.email;
    }

    public void updateProfileImageName(final String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public void updateQrImageName(final String qrImageName) {
        this.qrImageName = qrImageName;
    }

    public void updateIntroductoryArticle(final String introductoryArticle) {
        this.introductoryArticle = introductoryArticle;
    }

    public void updateQrLinkUrl(final String qrLinkUrl) {
        this.qrLinkUrl = qrLinkUrl;
    }

    public void register() {
        this.isRegistered = true;
    }

    public void updateIsConfirmedMailNotification(final Boolean isConfirmed) {
        this.isConfirmedMailNotification = isConfirmed;
    }

    public void updateDonationInfo(final String bankName, final String accountNumber, final String accountHolder,
                                   final String remitLink) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.remitLink = remitLink;
    }
}
