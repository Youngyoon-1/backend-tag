package com.tag.application.image;

import com.tag.dto.response.member.MemberImageUploadUrlResponse;

public abstract class ObjectStorageManager {

    protected abstract String createPresignedGetUrl(final String imageName);

    public abstract MemberImageUploadUrlResponse createPutUrl(final String fileType);

    public abstract void deleteObject(final String imageName);

    public String createGetUrl(final String imageName) {
        if (imageName != null) {
            return createPresignedGetUrl(imageName);
        }
        return null;
    }
}
