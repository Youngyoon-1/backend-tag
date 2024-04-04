package com.tag.presentation;

import com.tag.application.MemberImageCategory;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberImageCategoryConverterTest {

    private final MemberImageCategoryConverter memberImageCategoryConverter = new MemberImageCategoryConverter();

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #-----------------------------
            #    source    | ImageCategory
            #-----------------------------
                 profile    |    PROFILE
            #-----------------------------
                   qr      |      QR
            #-----------------------------
            """)
    void 문자열을_ImageCategory_타입으로_변환한다_(final String source, final MemberImageCategory expectation) {
        // when
        final MemberImageCategory memberImageCategory = memberImageCategoryConverter.convert(source);

        // then
        Assertions.assertThat(memberImageCategory).isEqualTo(expectation);
    }

    @Test
    void 문자열을_ImageCategory_타입으로_변환한다_유효하지_않은_문자열인_경우_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                        () -> memberImageCategoryConverter.convert("invalidSource")
                ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("문자열이 유효하지 않습니다.");

    }
}