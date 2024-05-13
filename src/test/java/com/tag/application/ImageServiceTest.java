//package com.tag.application;
//
//import static org.mockito.Mockito.verify;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class ImageServiceTest {
//
//    @Mock
//    private ObjectStorageManager objectStorageManager;
//
//    @InjectMocks
//    private ImageService imageService;
//
//    @Test
//    void 프로필_사진을_삭제한다() {
//        // given
//        BDDMockito.willDoNothing()
//                .given(objectStorageManager)
//                .deleteObject("profileImageName");
//
//        // when
//        imageService.deleteObject("profileImageName");
//
//        // then
//        verify(objectStorageManager).deleteObject("profileImageName");
//    }
//}
