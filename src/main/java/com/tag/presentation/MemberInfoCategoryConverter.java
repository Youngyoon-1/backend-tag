package com.tag.presentation;

import com.tag.application.MemberInfoCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class MemberInfoCategoryConverter implements Converter<String, MemberInfoCategory> {

    private static final String VIEW_INTRODUCTORY_ARTICLE = "introductoryArticle";
    private static final String VIEW_QR_LINK_URL = "qrLinkUrl";

    @Override
    public MemberInfoCategory convert(final String source) {
        if (VIEW_INTRODUCTORY_ARTICLE.equals(source)) {
            return MemberInfoCategory.INTRODUCTORY_ARTICLE;
        }
        if (VIEW_QR_LINK_URL.equals(source)) {
            return MemberInfoCategory.QR_LINK_URL;
        }
        throw new RuntimeException("문자열이 유효하지 않습니다.");
    }
}
