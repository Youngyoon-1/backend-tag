package com.tag.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ImagePathProvider {

    private static final String PATH_SEPARATOR = "/";

    private static final String FAIL_CREATE_IMAGE_PATH_INVALID_IMAGE_CATEGORY = "이미지 카테고리가 유효하지 않아 이미지 경로를 생성할 수 없습니다.";

    private final String profileImageFolderName;
    private final String qrImageFolderName;

    public ImagePathProvider(@Value("${object-storage.profile-image-folder-name}") final String profileImageFolderName,
                             @Value("${object-storage.qr-image-folder-name}") final String qrImageFolderName) {
        this.profileImageFolderName = profileImageFolderName;
        this.qrImageFolderName = qrImageFolderName;
    }

    public String create(final String imageName, final MemberImageCategory memberImageCategory) {
        if (memberImageCategory == MemberImageCategory.PROFILE) {
            return profileImageFolderName + PATH_SEPARATOR + imageName;
        }
        if (memberImageCategory == MemberImageCategory.QR) {
            return qrImageFolderName + PATH_SEPARATOR + imageName;
        }
        throw new IllegalArgumentException(FAIL_CREATE_IMAGE_PATH_INVALID_IMAGE_CATEGORY);
    }
}
