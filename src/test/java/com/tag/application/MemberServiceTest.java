package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tag.application.image.ObjectStorageManager;
import com.tag.application.member.MemberService;
import com.tag.application.member.searchStrategy.DonationInfoSearchStrategy;
import com.tag.application.member.searchStrategy.EmailSearchStrategy;
import com.tag.application.member.searchStrategy.IntroSearchStrategy;
import com.tag.application.member.searchStrategy.MailNotificationSearchStrategy;
import com.tag.application.member.searchStrategy.MemberSearchStrategy;
import com.tag.application.member.searchStrategy.ProfileImageNameSearchStrategy;
import com.tag.application.member.searchStrategy.ProfileImageUrlSearchStrategy;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.dto.request.member.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.member.MemberProfileUpdateRequest;
import com.tag.dto.response.member.MemberProfileUpdateResult;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ObjectStorageManager objectStorageManager;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        final List<MemberSearchStrategy> strategies = List.of(
                new DonationInfoSearchStrategy(),
                new EmailSearchStrategy(),
                new IntroSearchStrategy(),
                new MailNotificationSearchStrategy(),
                new ProfileImageNameSearchStrategy(),
                new ProfileImageUrlSearchStrategy(objectStorageManager)
        );
        memberService = new MemberService(memberRepository, objectStorageManager, strategies);
    }

    @Test
    void 회원_정보를_조회한다_모든_검색_카테고리_포함() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .introduction("introduction")
                .profileImageName("profileImageName")
                .bankName("testBank")
                .accountNumber("1234567890")
                .accountHolder("testHolder")
                .remitLink("https://remitLink.com")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));
        BDDMockito.given(objectStorageManager.createGetUrl("profileImageName"))
                .willReturn("profileImageUrl");

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("email", "introduction", "profileImageUrl", "profileImageName", "mailNotification",
                        "donationInfo"));

        // then
        final String profileImageUrl = memberResponse.getProfileImageUrl();
        assertAll(
                () -> assertThat(profileImageUrl).isEqualTo("profileImageUrl"),
                () -> assertThat(memberResponse).usingRecursiveComparison()
                        .ignoringFields("profileImageUrl")
                        .isEqualTo(member)
        );
    }

    @Test
    void 회원_정보를_조회한다_이메일() {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("email"));

        // then
        assertThat(memberResponse).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(member);
    }

    @Test
    void 회원_정보를_조회한다_자기소개글() {
        // given
        final Member member = Member.builder()
                .introduction("introduction")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("introduction"));

        // then
        assertThat(memberResponse).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(member);
    }

    @Test
    void 회원_정보를_조회한다_프로필_이미지_URL() {
        // given
        final Member member = Member.builder()
                .profileImageName("profileImageName")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));
        BDDMockito.given(objectStorageManager.createGetUrl("profileImageName"))
                .willReturn("profileImageUrl");

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("profileImageUrl"));

        // then
        final MemberResponse expected = MemberResponse.builder()
                .profileImageUrl("profileImageUrl")
                .build();
        assertThat(memberResponse).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(expected);
    }

    @Test
    void 회원_정보를_조회한다_프로필_이미지_이름() {
        // given
        final Member member = Member.builder()
                .profileImageName("profileImageName")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("profileImageName"));

        // then
        assertThat(memberResponse).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(member);
    }

    @Test
    void 회원_정보를_조회한다_이메일_알림_여부() {
        // given
        final Member member = Member.builder()
                .isConfirmedMailNotification(true)
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("mailNotification"));

        // then
        assertThat(memberResponse).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(member);
    }

    @Test
    void 회원_정보를_조회한다_후원_정보() {
        // given
        final Member member = Member.builder()
                .bankName("testBank")
                .accountNumber("1234567890")
                .accountHolder("testHolder")
                .remitLink("https://remitLink.com")
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        final MemberResponse memberResponse = memberService.findMember(1L,
                List.of("donationInfo"));

        // then
        assertThat(memberResponse).usingRecursiveComparison()
                .ignoringActualNullFields()
                .isEqualTo(member);
    }

    @Test
    void 회원_정보_조회시_존재하지_않는_회원일_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
                () -> memberService.findMember(1L, null)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }

    @Test
    void 회원을_등록한다() {
        // given
        final Member member = Member.builder()
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        memberService.registerMember(1L);

        // then
        final boolean registered = member.isRegistered();
        assertThat(registered).isTrue();
    }

    @Test
    void 회원을_등록한다_회원이_존재하지_않는_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
                () -> memberService.registerMember(1L)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }

    @Test
    void 회원_프로필을_수정한다_자기소개글_및_이미지_이름() {
        // given
        final Member member = Member.builder()
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));
        final MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest("introduction",
                "profileImageName");

        // when
        final MemberProfileUpdateResult memberProfileUpdateResult = memberService.updateMemberProfile(1L,
                memberProfileUpdateRequest);

        // then
        final String introduction = member.getIntroduction();
        final String profileImageName = member.getProfileImageName();
        final boolean profileImageUpdated = memberProfileUpdateResult.profileImageUpdated();
        final String previousProfileImageName = memberProfileUpdateResult.previousProfileImageName();
        assertAll(
                () -> assertThat(profileImageUpdated).isTrue(),
                () -> assertThat(previousProfileImageName).isNull(),
                () -> assertThat(introduction).isEqualTo("introduction"),
                () -> assertThat(profileImageName).isEqualTo("profileImageName")
        );
    }

    @Test
    void 회원_프로필을_수정한다_자기소개글() {
        // given
        final Member member = Member.builder()
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));
        final MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest("introduction",
                null);

        // when
        final MemberProfileUpdateResult memberProfileUpdateResult = memberService.updateMemberProfile(1L,
                memberProfileUpdateRequest);

        // then
        final boolean profileImageUpdated = memberProfileUpdateResult.profileImageUpdated();
        final String previousProfileImageName = memberProfileUpdateResult.previousProfileImageName();
        final String introduction = member.getIntroduction();
        assertAll(
                () -> assertThat(profileImageUpdated).isFalse(),
                () -> assertThat(previousProfileImageName).isNull(),
                () -> assertThat(introduction).isEqualTo("introduction")
        );
    }

    @Test
    void 회원_프로필을_수정한다_회원이_존재하지_않는_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
                () -> memberService.updateMemberProfile(1L, null)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }

    @Test
    void 이미지가_변경된_경우_이전_회원_이미지를_삭제한다() {
        // given
        final MemberProfileUpdateResult memberProfileUpdateResult = new MemberProfileUpdateResult(true,
                "previousProfileImageName");

        // when
        memberService.deleteMemberProfileImage(memberProfileUpdateResult);

        // then
        BDDMockito.verify(objectStorageManager)
                .deleteObject("previousProfileImageName");
    }

    @Test
    void 이미지가_변경되지_않은_경우_이미지를_삭제하지_않는다() {
        // given
        final MemberProfileUpdateResult memberProfileUpdateResult = new MemberProfileUpdateResult(false,
                null);

        // when
        memberService.deleteMemberProfileImage(memberProfileUpdateResult);

        // then
        BDDMockito.verify(objectStorageManager, BDDMockito.never())
                .deleteObject(BDDMockito.any());
    }

    @Test
    void 이전_이미지가_null_인_경우_해당_이미지를_삭제하지_않는다() {
        // given
        final MemberProfileUpdateResult memberProfileUpdateResult = new MemberProfileUpdateResult(true,
                null);

        // when
        memberService.deleteMemberProfileImage(memberProfileUpdateResult);

        // then
        BDDMockito.verify(objectStorageManager, BDDMockito.never())
                .deleteObject(BDDMockito.any());
    }

    @Test
    void 회원의_후원_정보를_수정한다() {
        // given
        final Member member = Member.builder()
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));
        final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest = new MemberDonationInfoUpdateRequest(
                "testBank",
                "1234567890", "testHolder", "https://remitLink.com");

        // when
        memberService.updateMemberDonationInfo(1L, memberDonationInfoUpdateRequest);

        // then
        assertThat(memberDonationInfoUpdateRequest).usingRecursiveComparison()
                .isEqualTo(member);
    }

    @Test
    void 회원의_후원정보를_수정한다_회원이_존재하지_않을_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
                () -> memberService.updateMemberDonationInfo(1L, null)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }

    @Test
    void 회원의_메일_알림_여부를_수정한다() {
        // given
        final Member member = Member.builder()
                .isConfirmedMailNotification(false)
                .build();
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        // when
        memberService.updateConfirmedMailNotification(1L, true);

        // then
        final boolean confirmedMailNotification = member.isConfirmedMailNotification();
        assertThat(confirmedMailNotification).isTrue();
    }

    @Test
    void 회원의_메일_알림_여부를_수정한다_회원이_존재하지_않을_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.findById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(
                () -> memberService.updateConfirmedMailNotification(1L, true)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }
}
