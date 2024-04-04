package com.tag.application;

import org.springframework.stereotype.Component;

@Component
public class ImageService {

    private final ObjectStorageManager objectStorageManager;

    public ImageService(final ObjectStorageManager objectStorageManager) {
        this.objectStorageManager = objectStorageManager;
    }

    public void deleteObject(final String profileImageName) {
        objectStorageManager.deleteObject(profileImageName);
    }
}
