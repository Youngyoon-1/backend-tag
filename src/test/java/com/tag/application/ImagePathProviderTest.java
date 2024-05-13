package com.tag.application;

import com.tag.application.image.ImagePathProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ImagePathProviderTest {

    private final ImagePathProvider imagePathProvider = new ImagePathProvider("profile-image");

    @Test
    void 프로필_이미지_경로를_생성한다() {
        // when
        final String profilePhotoPath = imagePathProvider.create("profileImageName");

        // then
        Assertions.assertThat(profilePhotoPath).isEqualTo("profile-image/profileImageName");
    }
}
