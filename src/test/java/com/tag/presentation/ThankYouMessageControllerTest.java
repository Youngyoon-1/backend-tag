package com.tag.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.application.AccessTokenProvider;
import com.tag.application.ThankYouMessageService;
import com.tag.dto.request.ThankYouMessageRequest;
import com.tag.dto.response.ThankYouMessageResponse;
import com.tag.dto.response.ThankYouMessagesResponse;
import java.util.List;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ThankYouMessageController.class)
public class ThankYouMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ThankYouMessageService thankYouMessageService;

    @MockBean
    private AccessTokenProvider accessTokenProvider;

    @Test
    void 감사_메시지_목록을_조회한다() throws Exception {
        // given
        final ThankYouMessageResponse thankYouMessageResponse1 = new ThankYouMessageResponse(2L, 10L,
                "thankYouMessage2Content");
        final ThankYouMessageResponse thankYouMessageResponse2 = new ThankYouMessageResponse(1L, 10L,
                "thankYouMessage1Content");
        final ThankYouMessagesResponse thankYouMessagesResponse = new ThankYouMessagesResponse(
                List.of(thankYouMessageResponse1, thankYouMessageResponse2));
        BDDMockito.given(thankYouMessageService.findThankYouMessages(10L, 2L, null))
                .willReturn(thankYouMessagesResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/members/10/thankYouMessages?pageSize=2")
        );

        // then
        final String serializedResponseContent = objectMapper.writeValueAsString(thankYouMessagesResponse);
        resultActions.andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().string(serializedResponseContent)
                )
                .andDo(MockMvcResultHandlers.print());
        BDDMockito.verify(thankYouMessageService)
                .findThankYouMessages(10L, 2L, null);
    }

    @Test
    void 감사_메시지_목록을_조회한다_조회할_감사_메세지의_첫_아이디가_주어진_경우() throws Exception {
        // given
        final ThankYouMessageResponse thankYouMessageResponse1 = new ThankYouMessageResponse(2L, 10L,
                "thankYouMessage2Content");
        final ThankYouMessageResponse thankYouMessageResponse2 = new ThankYouMessageResponse(1L, 10L,
                "thankYouMessage1Content");
        final ThankYouMessagesResponse thankYouMessagesResponse = new ThankYouMessagesResponse(
                List.of(thankYouMessageResponse1, thankYouMessageResponse2));
        BDDMockito.given(thankYouMessageService.findThankYouMessages(10L, 2L, 2L))
                .willReturn(thankYouMessagesResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/members/10/thankYouMessages?pageSize=2&fromId=2")
        );

        // then
        final String serializedResponseContent = objectMapper.writeValueAsString(thankYouMessagesResponse);
        resultActions.andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().string(serializedResponseContent)
                )
                .andDo(MockMvcResultHandlers.print());
        BDDMockito.verify(thankYouMessageService)
                .findThankYouMessages(10L, 2L, 2L);
    }

    @Test
    void 감사메세지를_저장한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        BDDMockito.willDoNothing()
                .given(thankYouMessageService)
                .saveThankYouMessage(10L, memberId, "thankYouMessageContent");

        // when
        final ThankYouMessageRequest thankYouMessageRequest = new ThankYouMessageRequest("thankYouMessageContent");
        final String serializedContent = objectMapper.writeValueAsString(thankYouMessageRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/thankYouMessages")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedContent)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider)
                        .getMemberId("accessToken"),
                () -> BDDMockito.verify(thankYouMessageService)
                        .saveThankYouMessage(10L, memberId, "thankYouMessageContent")
        );
    }

    @Test
    void 감사_메세지를_삭제한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        BDDMockito.willDoNothing()
                .given(thankYouMessageService)
                .deleteThankYouMessage(1L, 10L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/thankYouMessages/1")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider)
                        .getMemberId("accessToken"),
                () -> BDDMockito.verify(thankYouMessageService)
                        .deleteThankYouMessage(1L, 10L)
        );
    }
}
