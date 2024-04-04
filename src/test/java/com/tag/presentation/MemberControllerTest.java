package com.tag.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.application.AccessTokenProvider;
import com.tag.application.MemberImageCategory;
import com.tag.application.MemberInfoCategory;
import com.tag.application.MemberService;
import com.tag.domain.Member;
import com.tag.dto.request.MemberImageNameUpdateRequest;
import com.tag.dto.request.MemberInfoUpdateRequest;
import com.tag.dto.response.MemberImageUploadUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
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
        final MemberImageUploadUrlResponse memberImageUploadUrlResponse = new MemberImageUploadUrlResponse("profileUrl");
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
        final MemberImageUploadUrlResponse memberImageUploadUrlResponse = new MemberImageUploadUrlResponse("qrUrl");
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
}
