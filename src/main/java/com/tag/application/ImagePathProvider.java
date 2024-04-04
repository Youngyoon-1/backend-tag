package com.tag.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImagePathProvider {

    private static final String PATH_SEPARATOR = "/";

    private final String profileImageFolderName;
    private final String qrImageFolderName;

    public ImagePathProvider(@Value("${object-storage.profile-image-folder-name}") final String profileImageFolderName,
                             @Value("${object-storage.qr-image-folder-name}") final String qrImageFolderName) {
        this.profileImageFolderName = profileImageFolderName;
        this.qrImageFolderName = qrImageFolderName;
    }

    public String create(final String imageName, final MemberImageCategory memberImageCategory) {
        if (imageName == null) {
            throw new RuntimeException("이미지 경로를 생성할 수 없습니다.");
        }
        if (memberImageCategory == MemberImageCategory.PROFILE) {
            return profileImageFolderName + PATH_SEPARATOR + imageName;
        }
        if (memberImageCategory == MemberImageCategory.QR) {
            return qrImageFolderName + PATH_SEPARATOR + imageName;
        }
        throw new RuntimeException("이미지 경로를 생성할 수 없습니다.");
    }
}
