package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Service class for deleting images
 */
@Slf4j
@Service
public class DeleteImageFromServer {

    private static final String IMAGES = "images/";

    /**
     * Path to the folder where images are stored, loaded from application properties.
     */
    @Value("${external.images.path}")
    private String IMAGES_FOLDER_PATH;

    /**
     * Deletes an image from storage.
     *
     * @param imagePath the relative path of the image to be deleted
     * @return true if the image was deleted successfully or doesn't exist; false otherwise
     */
    public boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            log.info("File is empty or no image was uploaded");
            return true;
        }

        // Combine the base path with the provided image path
        String fullPath = IMAGES_FOLDER_PATH + imagePath.replaceFirst(IMAGES, "");

        // Delete the image file
        File file = new File(fullPath);
        if (file.exists()) {
            if (file.delete()) {
                log.info("File was deleted successfully");
                return true;
            } else {
                log.error("Failed to delete the file");
                return false;
            }
        } else {
            log.warn("File does not exist");
            return true;
        }
    }

}
