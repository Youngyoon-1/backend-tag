package com.tag.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.application.async.AsyncExecutor;
import com.tag.application.auth.AccessTokenProvider;
import com.tag.application.member.MemberService;
import com.tag.dto.request.member.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.member.MemberProfileUpdateRequest;
import com.tag.dto.response.member.MemberProfileUpdateResult;
import com.tag.presentation.member.MemberController;
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

    @MockBean
    private AsyncExecutor asyncExecutor;

    @Test
    void 회원의_후원정보를_수정한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(1L);
        final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest = new MemberDonationInfoUpdateRequest(
                "bankName",
                "1234567819", "accountHolder", "https://remit.kokoapay.com/1231231231");
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
                .given(asyncExecutor)
                .execute(any(Runnable.class));
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
                        .updateMemberProfile(eq(1L), any(MemberProfileUpdateRequest.class))
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
