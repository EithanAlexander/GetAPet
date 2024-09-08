package com.example.controller;

import com.example.utils.DatabaseService;
import com.example.utils.DeleteImageFromServer;
import com.example.utils.SaveImageToServer;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller class for handling server-side requests.
 * This class includes endpoints for user registration, login, fetching ads,
 * creating new ads, deleting ads, and managing user favorite ads.
 */
@RestController
@Slf4j
public class ServerController {
    private static final String KEY_VALUE_PATTERN = "Key \\((.+?)\\)=\\((.+?)\\)";
    private static final Pattern patternKey = Pattern.compile(KEY_VALUE_PATTERN);
    private final DatabaseService databaseService;
    private final SaveImageToServer saveImageToServer;
    private final DeleteImageFromServer deleteImageFromServer;

    @Autowired
    public ServerController(DatabaseService databaseService, SaveImageToServer saveImageToServer, DeleteImageFromServer deleteImageFromServer) {
        this.databaseService = databaseService;
        this.saveImageToServer = saveImageToServer;
        this.deleteImageFromServer = deleteImageFromServer;
    }

    /**
     * Registers a new user.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param displayName the display name of the user
     * @param email the email of the user
     * @param phone the phone number of the user
     * @return ResponseEntity with a success or an error message
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/api/register")
    public ResponseEntity<String> register(@RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                           @RequestParam("display_name") String displayName,
                                           @RequestParam("email") String email,
                                           @RequestParam("phone") String phone) {
        try {
            databaseService.createUser(username, password, displayName, email, phone);
            log.info("User created successfully: {}", username);
            return ResponseEntity.ok("User created successfully: " + username);
        } catch (DuplicateKeyException | PSQLException e) {
            String detailMessage = extractErrorDetail(e.getMessage());
            log.warn("Failed to create user - encountered a duplication, {}", e.getMessage());
            return ResponseEntity.badRequest().body("Chosen " + detailMessage + " exists, please choose a different one");
        } catch (Exception e) {
            log.error("Failed to create user, {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Enables user's login.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return ResponseEntity with the user profile or an error message
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password) {
        try {
            String userProfile = databaseService.isAllowedToLogin(username, password);
            if (userProfile != null) {
                    log.info("Successfully logged in user {}", username);
                    return ResponseEntity.ok(userProfile);
            }
        } catch (Exception e) {
            log.warn("Failed to login, {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to login: " + e.getMessage());
        }
        log.warn("Wrong user name or password, please try again");
        return ResponseEntity.badRequest().body("Wrong user name or password, please try again");
    }

    /**
     * Extracts specific details of an error to provide visibility in the frontend.
     *
     * @param message the getMessage from the error
     * @return String of details of a given error
     */
    private String extractErrorDetail(String message) {
        try {
            Matcher matcher = patternKey.matcher(message);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            log.error("Unexpected error when parsing an error message");
        }
        return "unknown error";
    }

    /**
     * Fetches all ads.
     *
     * @param pageNum the page number
     * @param adsPerPage the number of ads per page
     * @param category the category of the ads (optional)
     * @return ResponseEntity with a list of ads and the total number of ads
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/api/get_all_ads")
    public ResponseEntity<Object> getAllAds(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "adsPerPage", defaultValue = "10") int adsPerPage,
            @RequestParam(value = "category", required = false) String category) {
        try {
            List<DatabaseService.AdDetail> ads = databaseService.getGeneralAds(pageNum, adsPerPage, category);
            long totalAds = databaseService.getTotalAdsCount(category);
            log.info("Fetched total of {} ads", ads.size());
            return ResponseEntity.ok(Map.of("ads", ads, "totalAds", totalAds));
        } catch (Exception e) {
            log.error("Failed to fetch ads: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch ads");
        }
    }

    /**
     * Fetches ads created by a specific user.
     *
     * @param userId the user ID
     * @param pageNum the page number
     * @param adsPerPage the number of ads per page
     * @return ResponseEntity with a list of user ads and the total number of user ads
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/api/get_user_ads")
    public ResponseEntity<Object> getUserAds(@RequestParam("user_id") int userId,
                                             @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(value = "adsPerPage", defaultValue = "10") int adsPerPage) {
        try {
            List<DatabaseService.AdDetail> ads = databaseService.getUserAds(userId, pageNum, adsPerPage);
            long totalAds = databaseService.getTotalUserAdsCount(userId);
            log.info("User id {} created {} ads", userId, totalAds);
            return ResponseEntity.ok(Map.of("ads", ads, "totalAds", totalAds));
        } catch (Exception e) {
            log.error("Failed to fetch ads for user id {} {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch ads: " + e.getMessage());
        }
    }

    /**
     * Fetches favorite ads of a specific user.
     *
     * @param userId the user ID
     * @param pageNum the page number
     * @param adsPerPage the number of ads per page
     * @return ResponseEntity with a list of user favorite ads and the total number of user favorite ads
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/api/get_user_favorites_ads")
    public ResponseEntity<Object> getUserFavoritesAds(@RequestParam("user_id") int userId,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                      @RequestParam(value = "adsPerPage", defaultValue = "10") int adsPerPage) {
        try {
            List<DatabaseService.AdDetail> ads = databaseService.getUserFavoritesAds(userId, pageNum, adsPerPage);
            log.info("User id {} has {} favorite ads", userId, ads.size());
            long totalFavAds = databaseService.getTotalUserFavoriteAdsCount(userId);
            return ResponseEntity.ok(Map.of("ads", ads, "totalAds", totalFavAds));
        } catch (Exception e) {
            log.error("Failed to fetch ads for user id {} {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body("Failed to fetch ads: " + e.getMessage());
        }
    }

    /**
     * Creates a new ad.
     *
     * @param userName the username of the ad author
     * @param authorId the user ID of the ad author
     * @param category the category of the ad
     * @param petName the name of the pet
     * @param petAge the age of the pet
     * @param petGender the gender of the pet
     * @param adContent the content of the ad
     * @param imageFile the image file for the ad (optional)
     * @return ResponseEntity with a success or an error message
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/api/create_new_ad")
    public ResponseEntity<String> createNewAd(
            @RequestParam("user_name") String userName,
            @RequestParam("user_id") String authorId,
            @RequestParam("category") String category,
            @RequestParam("pet_name") String petName,
            @RequestParam("pet_age") Double petAge,
            @RequestParam("pet_gender") String petGender,
            @RequestParam("ad_content") String adContent,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        String msg = null;
        String fullImagePath = null;
        try {
            if(!databaseService.isAllowedToCreateAd(userName, authorId)) {
                return ResponseEntity.badRequest().body("Must be a registered user to create an ad");
            }
            String relativeImagePath = saveImageToServer.saveImage(imageFile);
            fullImagePath = relativeImagePath.isEmpty() ? "" : "/images/" + relativeImagePath; // Construct the full URL to the image
            int categoryId = databaseService.findCategoryIdByName(category);
            if (authorId != null) {
                databaseService.createNewAd(categoryId, Integer.parseInt(authorId), petName, petAge, petGender, adContent, fullImagePath);
                log.info("Ad created successfully for user {} and pet {}", userName, petName);
                return ResponseEntity.ok("Ad created successfully for pet " + petName);
            }
        } catch (Exception e) {
            msg = e.getMessage();
            log.error("Failed to create ad: {}", msg);
        }
        deleteImageFromServer.deleteImage(fullImagePath); //deleting the image if the ad was not created successfully
        return ResponseEntity.badRequest().body("Failed to create ad: " + msg);
    }

    /**
     * Deletes an ad.
     *
     * @param adId the ID of the ad to be deleted
     * @param imagePath the path of the image associated with the ad
     * @return ResponseEntity with a success or an error message
     */
    @CrossOrigin(origins = "*")
    @DeleteMapping("/api/delete_ad")
    public ResponseEntity<String> deleteAd(
            @RequestParam("ad_id") int adId,
            @RequestParam("image_path") String imagePath) {
        String msg = null;
        try {
            databaseService.deleteAd(adId);
            if (deleteImageFromServer.deleteImage(imagePath)) {
                log.info("Ad was deleted successfully");
                return ResponseEntity.ok("Ad was deleted successfully");
            }
        } catch (Exception e) {
            msg = e.getMessage();
            log.error("Failed to delete ad: {}", msg);
        }
        return ResponseEntity.badRequest().body("Failed to delete ad: " + msg);
    }

    /**
     * Adds an ad to the user's favorites.
     *
     * @param authorId the user ID
     * @param adId the ad ID
     * @return ResponseEntity with a success or an error message
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/api/add_ads_to_favorites")
    public ResponseEntity<Object> insertAdsToFavorites(@RequestParam("user_id") int authorId,
                                                       @RequestParam("ad_id") int adId) {
        try {
            databaseService.insertAdsToFavorites(authorId, adId);
            log.info("User id  {} now likes ad ID {} ", authorId, adId);
            return ResponseEntity.ok("Ad was successfully added to favorites");
        } catch (DuplicateKeyException | PSQLException e) {
            log.warn("Failed to add to favorites", e);
            return ResponseEntity.badRequest().body("Ad is already in favorites");
        } catch (Exception e) {
            log.error("Failed to add to favorites user id {} ad ID {} {}", authorId, adId, e.getMessage());
            return ResponseEntity.badRequest().body("Failed to add to favorites: " + extractErrorDetail(e.getMessage()));
        }
    }

    /**
     * Deletes an ad from the user's favorites.
     *
     * @param userId the user ID
     * @param adId the ad ID
     * @return ResponseEntity with a success or an error message
     */
    @CrossOrigin(origins = "*")
    @DeleteMapping("/api/delete_ad_from_favorites")
    public ResponseEntity<String> deleteAdFromFavorites(
            @RequestParam("user_id") int userId,
            @RequestParam("ad_id") int adId) {
        String msg = null;
        try {
            databaseService.deleteAdFromFavorites(userId, adId);
            log.info("Favorite ad was removed successfully from the favorites of the user");
            return ResponseEntity.ok("Favorite ad was removed successfully from the favorites of the user");
        } catch (Exception e) {
            msg = e.getMessage();
            log.error("Failed to removed favorite ad: {}", msg);
        }
        return ResponseEntity.badRequest().body("Failed to removed favorite ad: " + msg);
    }
}
