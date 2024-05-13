package com.tag;

import com.tag.application.image.ObjectStorageManager;
import com.tag.dto.response.member.MemberImageUploadUrlResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public final class FakeObjectManager extends ObjectStorageManager {

    @Override
    protected String createPresignedGetUrl(final String imageName) {
        return null;
    }

    @Override
    public MemberImageUploadUrlResponse createPutUrl(final String fileType) {
        return null;
    }

    @Override
    public void deleteObject(final String imageName) {

    }
}
