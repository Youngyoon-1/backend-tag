package com.tag.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.tag.application.image.ImagePathProvider;
import com.tag.application.image.OracleObjectManager;
import com.tag.dto.response.member.MemberImageUploadUrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class OracleObjectManagerTest {

    @Mock
    private ObjectStorageClient objectStorageClient;

    private OracleObjectManager oracleObjectManger;

    private final ImagePathProvider imagePathProvider = new ImagePathProvider("profile-image");

    @BeforeEach
    void setUp() {
        oracleObjectManger = new OracleObjectManager(
                objectStorageClient,
                imagePathProvider,
                "testBucket",
                "test",
                10000,
                "https://test.endPoint"
        );
    }

    @Test
    void 서명된_GET_URL_을_생성한다() {
        // given
        final PreauthenticatedRequest accessUri = PreauthenticatedRequest.builder()
                .accessUri("?accessUri")
                .build();
        final CreatePreauthenticatedRequestResponse preauthenticatedRequestResponse = CreatePreauthenticatedRequestResponse.builder()
                .preauthenticatedRequest(accessUri)
                .build();
        BDDMockito.given(objectStorageClient.createPreauthenticatedRequest(BDDMockito.any()))
                .willReturn(preauthenticatedRequestResponse);

        // when
        final String presignedUrl = oracleObjectManger.createGetUrl("imageName");

        // then
        assertThat(presignedUrl).isEqualTo("https://test.endPoint?accessUri");
    }

    @Test
    void 서명된_GET_URL_을_생성한다_이미지_이름이_null_인_경우_null_반환한다() {
        // when
        final String presignedUrl = oracleObjectManger.createGetUrl(null);

        // then
        assertThat(presignedUrl).isNull();
    }

    @Test
    void 서명된_PUT_URL_을_생성한다() {
        // given
        final PreauthenticatedRequest accessUri = PreauthenticatedRequest.builder()
                .accessUri("?accessUri")
                .build();
        final CreatePreauthenticatedRequestResponse preauthenticatedRequestResponse = CreatePreauthenticatedRequestResponse.builder()
                .preauthenticatedRequest(accessUri)
                .build();
        BDDMockito.given(objectStorageClient.createPreauthenticatedRequest(BDDMockito.any()))
                .willReturn(preauthenticatedRequestResponse);

        // when
        final MemberImageUploadUrlResponse response = oracleObjectManger.createPutUrl("jpg");

        // then
        final String url = response.getUrl();
        final String imageName = response.getImageName();
        assertAll(
                () -> assertThat(url).isEqualTo("https://test.endPoint?accessUri"),
                () -> assertThat(imageName).isNotNull()
        );
    }

    @Test
    void 객체를_삭제한다() {
        // when
        oracleObjectManger.deleteObject("imageName");

        // then
        BDDMockito.verify(objectStorageClient)
                .deleteObject(BDDMockito.any());
    }
}
