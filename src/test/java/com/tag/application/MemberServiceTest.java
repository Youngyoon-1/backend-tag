package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.dto.response.MemberImageGetUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
import com.tag.dto.response.MemberResponse;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3ObjectManager s3ObjectManager;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 회원_정보와_이미지를_조회한다() {
        // given
        final Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L, null);

        // then
        final String email = memberResponse.getEmail();
        final String introductoryArticle = memberResponse.getIntroductoryArticle();
        final String profilePhotoUrl = memberResponse.getProfileImageUrl();
        final String qrPhotoUrl = memberResponse.getQrImageUrl();
        final String qrLinkUrl = memberResponse.getQrLinkUrl();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(profilePhotoUrl).isNull(),
                () -> assertThat(qrPhotoUrl).isNull(),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> BDDMockito.verify(memberRepository)
                        .findById(1L)
        );
    }

    @Test
    void 회원_정보와_이미지를_조회한다_회원_조회_카테고리가_있는_경우() {
        // given
        final Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                "email, introductoryArticle, profileImageUrl, qrImageUrl, qrLinkUrl");

        // then
        final String email = memberResponse.getEmail();
        final String introductoryArticle = memberResponse.getIntroductoryArticle();
        final String profilePhotoUrl = memberResponse.getProfileImageUrl();
        final String qrPhotoUrl = memberResponse.getQrImageUrl();
        final String qrLinkUrl = memberResponse.getQrLinkUrl();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(profilePhotoUrl).isNull(),
                () -> assertThat(qrPhotoUrl).isNull(),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> BDDMockito.verify(memberRepository)
                        .findById(1L)
        );
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            #--------------------------------------------------------------
            #        searchCategory       |             value
            #--------------------------------------------------------------
                        email             |         test@test.com
            #--------------------------------------------------------------
                  introductoryArticle     |    introductoryArticleContent
            #--------------------------------------------------------------
                       qrLinkUrl          |           qrLinkUrl1
            #--------------------------------------------------------------
            """)
    void 회원_정보_또는_이미지를_조회한다_조회_카테고리가_있는_경우(final String searchCategory) {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .introduction("introductoryArticleContent")
                .qrLinkUrl("qrLinkUrl1")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L, searchCategory);

        // then
        final MemberResponse expectedMemberResponse = MemberResponse.builder()
                .email("test@test.com")
                .introductoryArticle("introductoryArticleContent")
                .qrLinkUrl("qrLinkUrl1")
                .build();
        Assertions.assertAll(
                () -> assertThat(memberResponse).usingRecursiveComparison()
                        .ignoringActualNullFields()
                        .isEqualTo(expectedMemberResponse),
                () -> BDDMockito.verify(memberRepository)
                        .findById(1L)
        );
    }

    @Test
    void 회원_정보를_조회한다_존재하지_않는_아이디인_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> memberService.findMember(1L, null)
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("존재하지 않는 아이디 입니다."),
                () -> BDDMockito.verify(memberRepository)
                        .findById(1L)
        );
    }

    @Test
    void 회원_프로필_이미지_사진을_수정한다() {
        // given
        final Member member = Member.builder()
                .id(10L)
                .build();
        BDDMockito.given(memberRepository.findById(10L))
                .willReturn(Optional.of(member));
        BDDMockito.given(s3ObjectManager.createPresignedGetUrl("profileImageName", MemberImageCategory.PROFILE))
                .willReturn("presignedProfileImageUrl");

        // when
        final MemberImageGetUrlResponse memberImageGetUrlResponse = memberService.updateImageName(10L,
                MemberImageCategory.PROFILE,
                "profileImageName");

        // then
        final String url = memberImageGetUrlResponse.getUrl();
        final String profileImageName = member.getProfileImageName();
        Assertions.assertAll(
                () -> assertThat(url).isEqualTo("presignedProfileImageUrl"),
                () -> assertThat(profileImageName).isEqualTo("profileImageName"),
                () -> BDDMockito.verify(memberRepository).findById(10L),
                () -> BDDMockito.verify(s3ObjectManager)
                        .createPresignedGetUrl("profileImageName", MemberImageCategory.PROFILE)
        );
    }

    @Test
    void 회원_큐알_이미지_사진을_수정한다() {
        // given
        final Member member = Member.builder()
                .id(10L)
                .build();
        BDDMockito.given(memberRepository.findById(10L))
                .willReturn(Optional.of(member));
        BDDMockito.given(s3ObjectManager.createPresignedGetUrl("qrImageName", MemberImageCategory.QR))
                .willReturn("presignedQrImageUrl");

        // when
        final MemberImageGetUrlResponse memberImageGetUrlResponse = memberService.updateImageName(10L, MemberImageCategory.QR,
                "qrImageName");

        // then
        final String url = memberImageGetUrlResponse.getUrl();
        final String qrImageName = member.getQrImageName();
        Assertions.assertAll(
                () -> assertThat(url).isEqualTo("presignedQrImageUrl"),
                () -> assertThat(qrImageName).isEqualTo("qrImageName"),
                () -> BDDMockito.verify(memberRepository).findById(10L),
                () -> BDDMockito.verify(s3ObjectManager)
                        .createPresignedGetUrl("qrImageName", MemberImageCategory.QR)
        );
    }

    @Test
    void 회원의_소개글을_수정한다() {
        // given
        final Member member = Member.builder()
                .build();
        BDDMockito.given(memberRepository.findById(10L))
                .willReturn(Optional.of(member));

        // when
        final MemberInfoUpdateResponse memberInfoUpdateResponse = memberService.updateMemberInfo(10L,
                MemberInfoCategory.INTRODUCTORY_ARTICLE, "introductoryArticleContent");

        // then
        final String content = memberInfoUpdateResponse.getContent();
        final String introductoryArticle = member.getIntroduction();
        final String qrLinkUrl = member.getQrLinkUrl();
        Assertions.assertAll(
                () -> assertThat(content).isEqualTo("introductoryArticleContent"),
                () -> assertThat(introductoryArticle).isEqualTo("introductoryArticleContent"),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> BDDMockito.verify(memberRepository).findById(10L)
        );
    }

    @Test
    void 회원의_큐알_링크_url_을_수정한다() {
        // given
        final Member member = Member.builder()
                .build();
        BDDMockito.given(memberRepository.findById(10L))
                .willReturn(Optional.of(member));

        // when
        final MemberInfoUpdateResponse memberInfoUpdateResponse = memberService.updateMemberInfo(10L,
                MemberInfoCategory.QR_LINK_URL, "qrLinkUrlContent");

        // then
        final String content = memberInfoUpdateResponse.getContent();
        final String qrLinkUrl = member.getQrLinkUrl();
        final String introductoryArticle = member.getIntroduction();
        Assertions.assertAll(
                () -> assertThat(content).isEqualTo("qrLinkUrlContent"),
                () -> assertThat(qrLinkUrl).isEqualTo("qrLinkUrlContent"),
                () -> assertThat(introductoryArticle).isNull(),
                () -> BDDMockito.verify(memberRepository).findById(10L)
        );
    }
}
