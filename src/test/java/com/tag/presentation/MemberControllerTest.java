package com.tag.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.application.AccessTokenProvider;
import com.tag.application.MemberImageCategory;
import com.tag.application.MemberInfoCategory;
import com.tag.application.MemberService;
import com.tag.domain.Member;
import com.tag.dto.request.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.MemberImageNameUpdateRequest;
import com.tag.dto.request.MemberInfoUpdateRequest;
import com.tag.dto.request.MemberProfileUpdateRequest;
import com.tag.dto.response.MemberDonationInfoResponse;
import com.tag.dto.response.MemberImageGetUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
import com.tag.dto.response.MemberProfileUpdateResult;
import com.tag.dto.response.MemberResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccessTokenProvider accessTokenProvider;

    @MockBean
    private MemberService memberService;

    @Test
    void 회원_정보를_조회한다() throws Exception {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        final MemberResponse memberResponse = new MemberResponse(member, null, null);
        BDDMockito.given(memberService.findMember(10L, null))
                .willReturn(memberResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/members/10")
        );

        // then
        final String serializedContent = objectMapper.writeValueAsString(memberResponse);
        resultActions.andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().string(serializedContent)
                )
                .andDo(MockMvcResultHandlers.print());
        BDDMockito.verify(memberService).findMember(10L, null);
    }

    @Test
    void 회원의_프로필_사진을_수정한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        final MemberImageGetUrlResponse memberImageUploadUrlResponse = new MemberImageGetUrlResponse("profileUrl");
        BDDMockito.given(memberService.updateImageName(10L, MemberImageCategory.PROFILE, "profileImageName"))
                .willReturn(memberImageUploadUrlResponse);

        // when
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "profileImageName");
        final String serializedRequestContent = objectMapper.writeValueAsString(memberImageNameUpdateRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch(
                                "/api/members/me/image-name?imageCategory=profile")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedRequestContent)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        final String expectedSerializedContent = objectMapper.writeValueAsString(memberImageUploadUrlResponse);
        resultActions.andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().string(expectedSerializedContent)
                )
                .andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService)
                        .updateImageName(10L, MemberImageCategory.PROFILE, "profileImageName")
        );
    }

    @Test
    void 회원의_큐알_사진을_수정한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        final MemberImageGetUrlResponse memberImageUploadUrlResponse = new MemberImageGetUrlResponse("qrUrl");
        BDDMockito.given(memberService.updateImageName(10L, MemberImageCategory.QR, "qrImageName"))
                .willReturn(memberImageUploadUrlResponse);

        // when
        final MemberImageNameUpdateRequest memberImageNameUpdateRequest = new MemberImageNameUpdateRequest(
                "qrImageName");
        final String serializedContentRequest = objectMapper.writeValueAsString(memberImageNameUpdateRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me/image-name?imageCategory=qr")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedContentRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        final String expectedSerializedContent = objectMapper.writeValueAsString(memberImageUploadUrlResponse);
        resultActions.andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().string(expectedSerializedContent)
                )
                .andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService).updateImageName(10L, MemberImageCategory.QR, "qrImageName")
        );
    }

    @Test
    void 회원의_소개글을_수정한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        final MemberInfoUpdateResponse memberInfoUpdateResponse = new MemberInfoUpdateResponse(
                "introductoryArticleContent");
        BDDMockito.given(memberService.updateMemberInfo(10L, MemberInfoCategory.INTRODUCTORY_ARTICLE,
                        "introductoryArticleContent"))
                .willReturn(memberInfoUpdateResponse);

        // when
        final MemberInfoUpdateRequest memberInfoUpdateRequest = new MemberInfoUpdateRequest(
                "introductoryArticleContent");
        final String serializedRequestContent = objectMapper.writeValueAsString(memberInfoUpdateRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me/info?infoCategory=introductoryArticle")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedRequestContent)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        final String serializedExpectedContent = objectMapper.writeValueAsString(memberInfoUpdateResponse);
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string(serializedExpectedContent)
        ).andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService)
                        .updateMemberInfo(10L, MemberInfoCategory.INTRODUCTORY_ARTICLE, "introductoryArticleContent")
        );
    }

    @Test
    void 큐알_링크_url_을_수정한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        final MemberInfoUpdateResponse memberInfoUpdateResponse = new MemberInfoUpdateResponse(
                "qrLinkUrlContent");
        BDDMockito.given(memberService.updateMemberInfo(10L, MemberInfoCategory.QR_LINK_URL,
                        "qrLinkUrlContent"))
                .willReturn(memberInfoUpdateResponse);

        // when
        final MemberInfoUpdateRequest memberInfoUpdateRequest = new MemberInfoUpdateRequest(
                "qrLinkUrlContent");
        final String serializedRequestContent = objectMapper.writeValueAsString(memberInfoUpdateRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me/info?infoCategory=qrLinkUrl")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedRequestContent)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        final String serializedExpectedContent = objectMapper.writeValueAsString(memberInfoUpdateResponse);
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string(serializedExpectedContent)
        ).andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService)
                        .updateMemberInfo(10L, MemberInfoCategory.QR_LINK_URL, "qrLinkUrlContent")
        );
    }

    @Test
    void 회원의_후원정보를_조회한다() throws Exception {
        // given
        final Member member = Member.builder()
                .bankName("bankName")
                .accountNumber("1234567819")
                .accountHolder("accountHolder")
                .remitLink("remitLink")
                .build();
        final MemberDonationInfoResponse memberDonationInfoResponse = new MemberDonationInfoResponse(member);
        BDDMockito.given(memberService.findDonationInfo(1L))
                .willReturn(memberDonationInfoResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/members/1/donation-info")
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(memberDonationInfoResponse))
        ).andDo(MockMvcResultHandlers.print());
        BDDMockito.verify(memberService).findDonationInfo(1L);
    }

    @Test
    void 회원의_후원정보를_수정한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(1L);
        final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest = new MemberDonationInfoUpdateRequest(
                "bankName",
                "1234567819", "accountHolder", "remitLink");
        BDDMockito.willDoNothing()
                .given(memberService)
                .updateMemberDonationInfo(1L, memberDonationInfoUpdateRequest);

        // when
        final String serializedRequestContent = objectMapper.writeValueAsString(memberDonationInfoUpdateRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me/donation-info")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedRequestContent)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isNoContent()
        ).andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService)
                        .updateMemberDonationInfo(eq(1L), any(MemberDonationInfoUpdateRequest.class))
        );
    }

    @Test
    void 회원_정보를_수정한다() throws Exception {
        // given
        final MemberProfileUpdateResult memberProfileUpdateResult = new MemberProfileUpdateResult(true,
                "profileImageName");
        BDDMockito.given(memberService.updateMemberProfile(eq(1L), any(MemberProfileUpdateRequest.class)))
                .willReturn(memberProfileUpdateResult);
        BDDMockito.willDoNothing()
                .given(memberService)
                .deleteMemberProfileImage(memberProfileUpdateResult);
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(1L);

        // when
        final MemberProfileUpdateRequest memberProfileUpdateRequest = new MemberProfileUpdateRequest("intro",
                "profileImageName");
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me/profile")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(objectMapper.writeValueAsString(memberProfileUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isNoContent()
        ).andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(memberService)
                        .updateMemberProfile(eq(1L), any(MemberProfileUpdateRequest.class)),
                () -> BDDMockito.verify(memberService).deleteMemberProfileImage(any(MemberProfileUpdateResult.class))
        );
    }

    @Test
    void 회원을_등록상태로_변경한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(1L);
        BDDMockito.willDoNothing()
                .given(memberService)
                .registerMember(1L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isNoContent()
        ).andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService).registerMember(1L)
        );
    }

    @Test
    void 회원_이미지_URL_을_조회한다() throws Exception {
        // given
        BDDMockito.given(memberService.getProfileImageUrl(1L))
                .willReturn("profileImageUrl");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/members/1/profile-image")
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string("profileImageUrl")
        ).andDo(MockMvcResultHandlers.print());
        BDDMockito.verify(memberService).getProfileImageUrl(1L);
    }

    @Test
    void 회원의_메일_알림_설정을_조회한다() throws Exception {
        // given
        BDDMockito.given(memberService.IsConfirmedMailNotification(1L))
                .willReturn(true);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/members/1/mail-notification")
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string("true")
        ).andDo(MockMvcResultHandlers.print());
        BDDMockito.verify(memberService).IsConfirmedMailNotification(1L);
    }

    @Test
    void 회원의_메일_알림_설정을_끈다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(1L);
        BDDMockito.willDoNothing()
                .given(memberService)
                .updateConfirmedMailNotification(1L, false);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/members/me/mail-notification?isConfirmed=false")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        );

        // then
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isNoContent()
        ).andDo(MockMvcResultHandlers.print());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken"),
                () -> BDDMockito.verify(memberService).updateConfirmedMailNotification(1L, false)
        );
    }
}
