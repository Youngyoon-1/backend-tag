package com.tag.presentation;

import com.tag.application.MemberImageCategory;
import com.tag.application.ObjectStorageManager;
import com.tag.dto.response.MemberImageUploadUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class ImageUploadUrlController {

    private final ObjectStorageManager objectStorageManager;

    public ImageUploadUrlController(final ObjectStorageManager objectStorageManager) {
        this.objectStorageManager = objectStorageManager;
    }

    @GetMapping("/api/image-upload-url")
    public ResponseEntity<MemberImageUploadUrlResponse> issueImageUploadUrl(
            // AccessTokenResolver 에서 유효성 검증을 위해서 사용
            @AccessTokenValue final long memberId,
            // ImageCategoryConverter 사용
            @RequestParam(name = "imageCategory") final MemberImageCategory memberImageCategory,
            @RequestParam(name = "fileType") final String fileType
    ) {
        final MemberImageUploadUrlResponse memberImageUploadUrlResponse = objectStorageManager.createPutUrl(memberImageCategory, fileType);
        return ResponseEntity.ok(memberImageUploadUrlResponse);
    }
}
