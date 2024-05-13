//package com.tag.application;
//
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.tag.dto.response.member.MemberImageUploadUrlResponse;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//
//public class S3ObjectManagerTest {
//
//    private final S3ObjectManager s3ObjectManager = new S3ObjectManager(
//            S3Presigner.builder()
//                    .credentialsProvider(StaticCredentialsProvider.create(
//                            AwsBasicCredentials.create(
//                                    "accessKey",
//                                    "secretKey")))
//                    .region(Region.AP_NORTHEAST_2)
//                    .build(),
//            null,
//            new ImagePathProvider("profile-image", "qr-image"),
//            "bucketName",
//            1000
//    );
//
//    @Test
//    void 서명된_GET_URL_을_생성한다() {
//        // when
//        final String presignedUrl = s3ObjectManager.createPresignedGetUrl("fileName",
//                MemberImageCategory.PROFILE);
//
//        // then
//        // 버킷이름, 리전, 파일이름, X-Amz-Expires, X-Amz-Credential(엑세스 키) 검증
//        Assertions.assertAll(
//                () -> assertThat(presignedUrl).contains("bucketName"),
//                () -> assertThat(presignedUrl).contains("ap-northeast-2"),
//                () -> assertThat(presignedUrl).contains("profile-image/fileName"),
//                // 초
//                () -> assertThat(presignedUrl).contains("X-Amz-Expires=1"),
//                () -> assertThat(presignedUrl).contains("X-Amz-Credential=accessKey")
//        );
//    }
//
//    @Test
//    void 서명된_PUT_URL_을_생성한다() {
//        // when
//        final MemberImageUploadUrlResponse imageUploadUrlResponse = s3ObjectManager.createPutUrl(
//                MemberImageCategory.QR, "png");
//
//        // then
//        // 버킷이름, 리전, 파일경로, X-Amz-Expires, X-Amz-Credential(엑세스 키) 검증
//        final String presignedUrl = imageUploadUrlResponse.getUrl();
//        Assertions.assertAll(
//                () -> assertThat(presignedUrl).contains("bucketName"),
//                () -> assertThat(presignedUrl).contains("ap-northeast-2"),
//                () -> assertThat(presignedUrl).contains("qr-image/"),
//                // 초
//                () -> assertThat(presignedUrl).contains("X-Amz-Expires=1"),
//                () -> assertThat(presignedUrl).contains("X-Amz-Credential=accessKey")
//        );
//    }
//}
