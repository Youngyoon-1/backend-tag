package com.tag.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.application.AccessTokenProvider;
import com.tag.application.CommentService;
import com.tag.dto.request.CommentRequest;
import com.tag.dto.response.CommentCountResponse;
import com.tag.dto.response.CommentResponse;
import com.tag.dto.response.CommentsResponse;
import java.util.List;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AccessTokenProvider accessTokenProvider;

    @Test
    void 댓글을_저장한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        BDDMockito.willDoNothing()
                .given(commentService)
                .saveComment(1L, 10L, "commentContent");

        // when
        final CommentRequest commentRequest = new CommentRequest("commentContent");
        final String serializedContent = objectMapper.writeValueAsString(commentRequest);
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/thankYouMessages/1/comments")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .content(serializedContent)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider)
                        .getMemberId("accessToken"),
                () -> BDDMockito.verify(commentService)
                        .saveComment(1L, 10L, "commentContent")
        );
    }

    @Test
    void 댓글을_삭제한다() throws Exception {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);
        BDDMockito.willDoNothing()
                .given(commentService)
                .deleteComment(1L, 10L);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/comments/1")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        );

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());
        Assertions.assertAll(
                () -> BDDMockito.verify(accessTokenProvider)
                        .getMemberId("accessToken"),
                () -> BDDMockito.verify(commentService)
                        .deleteComment(1L, 10L)
        );
    }

    @Test
    void 댓글_목록을_조회한다() throws Exception {
        // given
        final CommentResponse commentResponse = new CommentResponse(1L, 1L, "comment");
        final CommentsResponse commentsResponse = new CommentsResponse(List.of(commentResponse));
        BDDMockito.given(commentService.findComments(10L, 1L, null))
                .willReturn(commentsResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/thankYouMessages/10/comments?pageSize=1")
        );

        // then
        final String serializedContent = objectMapper.writeValueAsString(commentsResponse);
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string(serializedContent)
        );
        BDDMockito.verify(commentService)
                .findComments(10L, 1L, null);
    }

    @Test
    void 댓글_목록을_조회한다_조회를_시작할_댓글_아이디가_주어진_경우() throws Exception {
        // given
        final CommentResponse commentResponse = new CommentResponse(1L, 1L, "comment");
        final CommentsResponse commentsResponse = new CommentsResponse(List.of(commentResponse));
        BDDMockito.given(commentService.findComments(10L, 1L, 1L))
                .willReturn(commentsResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/thankYouMessages/10/comments?pageSize=1&fromId=1")
        );

        // then
        final String serializedContent = objectMapper.writeValueAsString(commentsResponse);
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string(serializedContent)
        );
        BDDMockito.verify(commentService)
                .findComments(10L, 1L, 1L);
    }

    @Test
    void 감사메세지_아이디로_댓글_개수를_조회한다() throws Exception {
        // given
        final CommentCountResponse commentCountResponse = new CommentCountResponse(1L);
        BDDMockito.given(commentService.findCommentCount(5L))
                .willReturn(commentCountResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/thankYouMessages/5/comments/count")
        );

        // then
        final String serializedContent = objectMapper.writeValueAsString(commentCountResponse);
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().string(serializedContent)
        );
        BDDMockito.verify(commentService)
                .findCommentCount(5L);
    }
}
