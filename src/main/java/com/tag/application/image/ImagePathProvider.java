package com.tag.application.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ImagePathProvider {

    private static final String PATH_SEPARATOR = "/";

    private final String profileImageFolderName;

    public ImagePathProvider(
            @Value("${object-storage.profile-image-folder-name}") final String profileImageFolderName) {
        this.profileImageFolderName = profileImageFolderName;
    }

    public String create(final String imageName) {
        return profileImageFolderName + PATH_SEPARATOR + imageName;
    }
}
