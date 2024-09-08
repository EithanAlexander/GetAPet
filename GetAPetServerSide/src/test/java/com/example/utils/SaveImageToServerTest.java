package com.example.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class SaveImageToServerTest {

    @InjectMocks
    private SaveImageToServer saveImageToServer;
    @Mock
    MultipartFile file;
    private static final String imageFilePath = "src/test/resources/Aslan1MB.jpeg";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveImageEmptyFile() throws IOException {
        String res = saveImageToServer.saveImage(null);
        assertTrue(res.isEmpty());
    }

    @Test
    void testSaveImageFileNotImage() {
        assertThrows(IOException.class, () -> {
            saveImageToServer.saveImage(file);
        });
    }

    @Test
    void testFileLargeImage() throws IOException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(imageFilePath));
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        when(file.getBytes()).thenReturn(imageBytes);
        when(file.getSize()).thenReturn((long) imageBytes.length);
        when(file.getOriginalFilename()).thenReturn("test-image.png");
        when(file.getContentType()).thenReturn("image/png");
        when(file.getInputStream()).thenReturn(inputStream);
        assertThrows(IOException.class, () -> {
            saveImageToServer.saveImage(file);
        });
    }

}
