//package com.tag.presentation;
//
//import static com.tag.application.auth.AccessTokenProvider.TOKEN_TYPE;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tag.application.auth.AccessTokenProvider;
//import com.tag.application.MemberImageCategory;
//import com.tag.dto.response.member.MemberImageUploadUrlResponse;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.mockito.BDDMockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpHeaders;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//@WebMvcTest(ImageUploadUrlController.class)
//class ImageUploadUrlControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private AccessTokenProvider accessTokenProvider;
//
//    @MockBean
//    private S3ObjectManager s3ObjectManager;
//
//    @Test
//    void 이미지_업로드용_url_을_발급한다() throws Exception {
//        // given
//        final String authHeader = TOKEN_TYPE + " " + "accessToken";
////        BDDMockito.willDoNothing()
////                .given(accessTokenProvider)
////                .validateAuthHeader(authHeader);
//        final MemberImageUploadUrlResponse imageUploadUrlResponse = new MemberImageUploadUrlResponse(
//                "profile-image/profileImageName",
//                "imageName");
//        BDDMockito.given(
//                        s3ObjectManager.createPutUrl(MemberImageCategory.PROFILE, "png"))
//                .willReturn(imageUploadUrlResponse);
//
//        // when
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);
//        final ResultActions resultActions = mockMvc.perform(
//                MockMvcRequestBuilders.get("/api/image-upload-url?imageCategory=profile&fileType=png")
//                        .headers(httpHeaders)
//        );
//
//        // then
//        final MemberImageUploadUrlResponse expectedMemberImageUploadUrlResponse = new MemberImageUploadUrlResponse(
//                "profile-image/profileImageName", "imageName");
//        final String expectedSerializedContent = objectMapper.writeValueAsString(expectedMemberImageUploadUrlResponse);
//        resultActions.andExpectAll(
//                MockMvcResultMatchers.status().isOk(),
//                MockMvcResultMatchers.content().string(expectedSerializedContent)
//        );
//        Assertions.assertAll(
//                () -> BDDMockito.verify(s3ObjectManager)
//                        .createPutUrl(MemberImageCategory.PROFILE, "png")
////                () -> BDDMockito.verify(accessTokenProvider)
////                        .validateAuthHeader(authHeader)
//        );
//    }
//}
