package com.tag.application;

import com.tag.dto.response.MemberImageUploadUrlResponse;

public abstract class ObjectStorageManager {

    abstract String createPresignedGetUrl(final String imageName,
                                          final MemberImageCategory memberImageCategory);

    public abstract MemberImageUploadUrlResponse createPutUrl(final MemberImageCategory memberImageCategory,
                                                              final String fileType);

    public abstract void deleteObject(final String profileImageName);


    public String createGetUrl(final String imageName, final MemberImageCategory memberImageCategory) {
        if (imageName != null) {
            return createPresignedGetUrl(imageName, memberImageCategory);
        }
        return null;
    }
}
