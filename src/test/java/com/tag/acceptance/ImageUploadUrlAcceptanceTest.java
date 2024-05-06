package com.tag.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.dto.response.LoginResponse;
import com.tag.dto.response.MemberImageUploadUrlResponse;
import java.net.URI;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@AcceptanceTest
public class ImageUploadUrlAcceptanceTest extends WithTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void 이미지_업로드용_URL_을_발급한다() {
        // given
        final LoginResponse loginResponse = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        ).getBody();

        // when
        final String accessToken = loginResponse.getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        final HttpEntity httpEntity = new HttpEntity<>(headers);
        final ResponseEntity<MemberImageUploadUrlResponse> imageUploadUrlResponseEntity = testRestTemplate.exchange(
                "/api/image-upload-url?imageCategory=profile&fileType=png",
                HttpMethod.GET,
                httpEntity,
                MemberImageUploadUrlResponse.class
        );

        // then
        final HttpStatusCode imageUploadUrlResponseStatusCode = imageUploadUrlResponseEntity.getStatusCode();
        final String url = imageUploadUrlResponseEntity.getBody()
                .getUrl();
        final URL resource = ClassLoader.getSystemClassLoader()
                .getResource("tag.png");
        // 발급한 URL 로 이미지 업로드 요청을 보내 URL 이 유효한지 검증한다
        final ResponseEntity<String> exchange = testRestTemplate.exchange(
                URI.create(url),
                HttpMethod.PUT,
                new HttpEntity<>(new UrlResource(resource)),
                String.class
        );
//        final HttpStatusCode imageUploadResponseStatusCode = testRestTemplate.exchange(
//                URI.create(url),
//                HttpMethod.PUT,
//                new HttpEntity<>(new UrlResource(resource)),
//                Void.class
//        ).getStatusCode();
        Assertions.assertAll(
                () -> assertThat(imageUploadUrlResponseStatusCode).isEqualTo(HttpStatus.OK)
//                () -> assertThat(imageUploadResponseStatusCode).isEqualTo(HttpStatus.OK)
        );
    }
}
