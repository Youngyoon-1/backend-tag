package com.tag.dto.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberSearchCategoryTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #----------------------------------------------
            #    searchCategory   |      expectation
            #----------------------------------------------
                      email       |         true
            #----------------------------------------------
                      amail       |         false
            #----------------------------------------------
            """)
    void 파라미터에_이메일이_포함되어_있는지_판별한다(final String searchCategory, final Boolean expectation) {
        // when
        final boolean hasEmail = MemberSearchCategory.hasEmailFromParam(searchCategory);

        // then
        Assertions.assertThat(hasEmail).isEqualTo(expectation);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #----------------------------------------------
            #    searchCategory      |      expectation
            #----------------------------------------------
               introductoryArticle   |        true
            #----------------------------------------------
                      intro          |        false
            #----------------------------------------------
            """)
    void 파라미터에_소개글이_포함되어_있는지_판별한다(final String searchCategory, final Boolean expectation) {
        // when
        final boolean hasIntroductoryArticle = MemberSearchCategory.hasIntroductionFromParam(searchCategory);

        // then
        Assertions.assertThat(hasIntroductoryArticle).isEqualTo(expectation);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #----------------------------------------------
            #    searchCategory     |      expectation
            #----------------------------------------------
                  profileImageUrl    |         true
            #----------------------------------------------
                  profilePhotoUrl    |         false
            #----------------------------------------------
            """)
    void 파라미터에_프로필_이미지_url_이_포함되어_있는지_판별한다(final String searchCategory, final Boolean expectation) {
        // when
        final boolean hasProfileImageUrl = MemberSearchCategory.hasProfileImageUrlFromParam(searchCategory);

        // then
        Assertions.assertThat(hasProfileImageUrl).isEqualTo(expectation);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #----------------------------------------------
            #    searchCategory   |      expectation
            #----------------------------------------------
                   qrImageUrl     |         true
            #----------------------------------------------
                   qrPhotoUrl     |         false
            #----------------------------------------------
            """)
    void 파라미터에_큐알_이미지_url_이_포함되어_있는지_판별한다(final String searchCategory, final Boolean expectation) {
        // when
        final boolean hasQrImageUrl = MemberSearchCategory.hasQrImageUrlFromParam(searchCategory);

        // then
        Assertions.assertThat(hasQrImageUrl).isEqualTo(expectation);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #----------------------------------------------
            #    searchCategory   |      expectation
            #----------------------------------------------
                   qrLinkUrl      |         true
            #----------------------------------------------
                      qrUrl       |         false
            #----------------------------------------------
            """)
    void 파라미터에_큐알_링크_url_이_포함되어_있는지_판별한다(final String searchCategory, final Boolean expectation) {
        // when
        final boolean qrLinkUrl = MemberSearchCategory.hasQrLinkUrlFromParam(searchCategory);

        // then
        Assertions.assertThat(qrLinkUrl).isEqualTo(expectation);
    }
}