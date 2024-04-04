package com.tag.presentation;

import com.tag.application.AccessTokenProvider;
import com.tag.application.MemberImageCategory;
import com.tag.application.ObjectStorageManager;
import com.tag.dto.response.MemberImageUploadUrlResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageUploadUrlController {

    private final ObjectStorageManager objectStorageManager;
    private final AccessTokenProvider accessTokenProvider;

    public ImageUploadUrlController(final ObjectStorageManager objectStorageManager,
                                    final AccessTokenProvider accessTokenProvider) {
        this.objectStorageManager = objectStorageManager;
        this.accessTokenProvider = accessTokenProvider;
    }

    @GetMapping("/api/image-upload-url")
    public ResponseEntity<MemberImageUploadUrlResponse> issueImageUploadUrl(final HttpServletRequest request,
                                                                            // ImageCategoryConverter 사용
                                                                            @RequestParam(name = "imageCategory") final MemberImageCategory memberImageCategory,
                                                                            @RequestParam(name = "fileType") final String fileType) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        accessTokenProvider.validateAuthHeader(authHeader);
        final MemberImageUploadUrlResponse memberImageUploadUrlResponse = objectStorageManager.createPresignedPutUrl(memberImageCategory, fileType);
        return ResponseEntity.ok(memberImageUploadUrlResponse);
    }
}
