package com.tag.application;

import com.tag.dto.response.MemberImageUploadUrlResponse;

public interface ObjectStorageManager {

    String createPresignedGetUrl(final String imageName, final MemberImageCategory memberImageCategory);

    MemberImageUploadUrlResponse createPresignedPutUrl(final MemberImageCategory memberImageCategory,
                                                       final String fileType);
    void deleteObject(final String profileImageName);
}
