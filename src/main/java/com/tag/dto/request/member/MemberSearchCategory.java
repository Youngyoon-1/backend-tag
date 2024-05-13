package com.tag.dto.request.member;

public enum MemberSearchCategory {

    VIEW_EMAIL("email"),
    VIEW_INTRODUCTION("introduction"),
    VIEW_PROFILE_IMAGE_URL("profileImageUrl"),
    VIEW_PROFILE_IMAGE_NAME("profileImageName"),
    VIEW_MAIL_NOTIFICATION("mailNotification"),
    VIEW_DONATION_INFO("donationInfo");

    public final String value;

    MemberSearchCategory(final String value) {
        this.value = value;
    }
}
