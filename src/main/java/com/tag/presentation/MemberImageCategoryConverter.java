package com.tag.presentation;

import com.tag.application.MemberImageCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MemberImageCategoryConverter implements Converter<String, MemberImageCategory> {

    private static final String VIEW_PROFILE = "profile";
    private static final String VIEW_QR = "qr";

    @Override
    public MemberImageCategory convert(final String source) {
        if (VIEW_PROFILE.equals(source)) {
            return MemberImageCategory.PROFILE;
        }
        if (VIEW_QR.equals(source)) {
            return MemberImageCategory.QR;
        }
        throw new RuntimeException("문자열이 유효하지 않습니다.");
    }
}
