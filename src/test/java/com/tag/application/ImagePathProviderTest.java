package com.tag.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ImagePathProviderTest {

    private final ImagePathProvider imagePathProvider = new ImagePathProvider("profile-image", "qr-image");

    @Test
    void 프로필_이미지_경로를_생성한다() {
        // when
        final String profilePhotoPath = imagePathProvider.create("profileImageName", MemberImageCategory.PROFILE);

        // then
        Assertions.assertThat(profilePhotoPath).isEqualTo("profile-image/profileImageName");
    }

    @Test
    void QR_이미지_경로를_생성한다() {
        // when
        final String qrPhotoName = imagePathProvider.create("qrImageName", MemberImageCategory.QR);

        // then
        Assertions.assertThat(qrPhotoName).isEqualTo("qr-image/qrImageName");
    }

    @Test
    void 이미지_이름이_null_이면_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                        () -> imagePathProvider.create(null, MemberImageCategory.PROFILE)
                ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("이미지 경로를 생성할 수 없습니다.");
    }

    @Test
    void 이미지_카테고리가_null_이면_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                        () -> imagePathProvider.create("imageName", null)
                ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("이미지 경로를 생성할 수 없습니다.");
    }
}
