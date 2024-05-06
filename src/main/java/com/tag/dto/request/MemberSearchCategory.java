package com.tag.dto.request;

public enum MemberSearchCategory {

    VIEW_EMAIL("email"),
    VIEW_INTRODUCTORY_ARTICLE("introductoryArticle"),
    VIEW_PROFILE_IMAGE_URL("profileImageUrl"),
    VIEW_QR_IMAGE_URL("qrImageUrl"),
    VIEW_QR_LINK_URL("qrLinkUrl"),
    ;

    private final String value;

    MemberSearchCategory(final String value) {
        this.value = value;
    }

    public static boolean hasEmailFromParam(final String memberSearchCategory) {
        return memberSearchCategory.contains(VIEW_EMAIL.value);
    }

    public static boolean hasIntroductionFromParam(final String memberSearchCategory) {
        return memberSearchCategory.contains(VIEW_INTRODUCTORY_ARTICLE.value);
    }

    public static boolean hasProfileImageUrlFromParam(final String memberSearchCategory) {
        return memberSearchCategory.contains(VIEW_PROFILE_IMAGE_URL.value);
    }

    public static boolean hasQrImageUrlFromParam(final String memberSearchCategory) {
        return memberSearchCategory.contains(VIEW_QR_IMAGE_URL.value);
    }

    public static boolean hasQrLinkUrlFromParam(final String memberSearchCategory) {
        return memberSearchCategory.contains(VIEW_QR_LINK_URL.value);
    }
}
