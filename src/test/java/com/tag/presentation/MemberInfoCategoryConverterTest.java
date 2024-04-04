package com.tag.presentation;

import com.tag.application.MemberInfoCategory;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MemberInfoCategoryConverterTest {

    private final MemberInfoCategoryConverter memberInfoCategoryConverter = new MemberInfoCategoryConverter();

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #----------------------------------------------
            #        source        |     InfoCategory
            #----------------------------------------------
               introductoryArticle |  INTRODUCTORY_ARTICLE
            #----------------------------------------------
                   qrLinkUrl       |      QR_LINK_URL
            #----------------------------------------------
            """)
    void 문자열을_MemgerInfoCategory_타입으로_변환한다_(final String source, final MemberInfoCategory expectation) {
        // given, when
        final MemberInfoCategory infoCategory = memberInfoCategoryConverter.convert(source);

        // then
        Assertions.assertThat(infoCategory).isEqualTo(expectation);
    }

    @Test
    void 문자열을_ImageCategory_타입으로_변환한다_유효하지_않은_문자열인_경우_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                        () -> memberInfoCategoryConverter.convert("invalidSource")
                ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("문자열이 유효하지 않습니다.");

    }
}
