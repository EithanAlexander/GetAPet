package com.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

/**
 * Service class for saving images to the server with validation and storage handling.
 * This class ensures that uploaded images meet specified criteria for file type and size,
 * renames files to avoid naming collisions, and saves them to a configured directory.
 * Utilizes Spring's @Value for configuration.
 */
@Service
@Slf4j
public class SaveImageToServer {

    @Value("${external.images.path}")
    private String IMAGES_FOLDER_PATH;
    private static final long MAX_SIZE_FILE = 1048576; // Maximum file size (1 MB = 1048576 bytes)
    private static final List<String> FILE_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png");

    /**
     * Saves image in storage
     *
     * @param file uploaded when creating a new Ad
     * @return path of the saved image from the uploaded file if matched the size and type criteria
     * @throws IOException in case there was an issue with saving the image
     */
    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("File is empty or no image was uploaded");
            return "";
        }
        if (!isFileValidImage(file)) {
            log.error("File is not a valid image");
            throw new IOException("File is not a valid image, please use jpg/jpeg/png formats only");
        }
        if (!isNotValidImageSize(file)) {
            log.error("Image size is larger than 1MB");
            throw new IOException("Image size is larger than 1MB, please upload a smaller image");
        }

        // Generate a safe filename with spaces replaced by underscores
        String originalFilename = file.getOriginalFilename();
        String safeFilename = originalFilename != null ? originalFilename.replaceAll("\\s", "_") : "image_" + System.currentTimeMillis();
        Path path = Paths.get(IMAGES_FOLDER_PATH);
        Files.createDirectories(path); // Ensure the directory exists

        Path filePath = path.resolve(safeFilename);
        filePath = resolveFileNameCollision(filePath);

        // Copy the image file to the resolved path
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Construct the relative image path within the images directory
        return path.relativize(filePath).toString();
    }

    /**
     * Updating image name if there is already an existing one with the same name
     *
     * @param filePath where the file will be stored
     * @return the final path of the file
     */
    private Path resolveFileNameCollision(Path filePath) {
        Path targetPath = filePath;
        int count = 1;
        String fileName = filePath.getFileName().toString();
        String baseName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.')) : "";

        while (Files.exists(targetPath)) {
            String newFileName = baseName + "_" + count + extension;
            targetPath = filePath.getParent().resolve(newFileName);
            count++;
        }
        log.info("final path for {} is {}" , fileName , targetPath);
        return targetPath;
    }

    /**
     * Validating the max file size allowed to upload
     *
     * @param file image uploaded by the user
     * @return true if the file is null or size is bigger than MAX_SIZE_FILE, else false
     */
    private boolean isNotValidImageSize(MultipartFile file) {
        return !(file == null || file.getSize() > MAX_SIZE_FILE);
    }

    /**
     * Determine if a file is an image
     *
     * @param file uploaded by the user
     * @return true if a valid image type else false
     */
    private boolean isFileValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !FILE_TYPES.contains(contentType)) {
            return false;
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

}
