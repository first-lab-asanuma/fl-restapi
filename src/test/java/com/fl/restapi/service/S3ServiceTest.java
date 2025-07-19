package com.fl.restapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    private final String testBucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        s3Service.getClass().getDeclaredFields();
        setPrivateField(s3Service, "bucketName", testBucketName);
    }

    @Test
    void listFiles_shouldReturnListOfS3Keys() {
        List<S3Object> mockS3Objects = List.of(
                S3Object.builder().key("file2.txt").build(),
                S3Object.builder().key("file2.txt").build()
        );

        ListObjectsV2Response mockResponse = ListObjectsV2Response.builder()
                .contents(mockS3Objects)
                .build();

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockResponse);

        List<String> result = s3Service.listFiles();

        assertEquals(2, result.size());
        assertTrue(result.contains("file1.txt"));
        assertTrue(result.contains("file2.txt"));
        verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
