package com.example.utils;

import com.google.gson.Gson;
import com.lambdaworks.crypto.SCryptUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Service class for handling database operations.
 * This class includes methods for user management, ad management, and utility functions
 * related to database operations.
 */
@Service
@Slf4j
public class DatabaseService {
    private final ExecuteQuery eq;
    private final Gson gson = new Gson();
    private static final int RETRY = 0;

    @Autowired
    public DatabaseService(ExecuteQuery executeQuery) {
        this.eq = executeQuery;
    }

    public record UserProfile(int id, String username, String display_name, String email, String phone) {}

    public record AdDetail(int ad_id, String display_name, String email, String phone, String pet_name, String category, int pet_age, String pet_gender, String ad_content, String image_path, LocalDateTime created_at) {}

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[\\d-]+$");

    static final String SQL_CREATE_NEW_AD = "INSERT INTO ads (category_id, author_id, pet_name, pet_age, pet_gender, ad_content, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
    static final String SQL_DELETE_AD = "DELETE FROM ads WHERE id = ?";
    static final String SQL_DELETE_FAVORITE_AD = "DELETE FROM favorites WHERE ad_id = ?";
    static final String SQL_DELETE_USER_FAVORITE_AD = "DELETE FROM favorites WHERE user_id = ? and ad_id = ?";
    static final String SQL_CREATE_NEW_USER = "INSERT INTO users (username, password, display_name, email, phone) VALUES (?, ?, ?, ?, ?)";
    static final String SQL_CREATE_NEW_FAVORITE_AD = "INSERT INTO favorites (user_id, ad_id) VALUES (?, ?)";
    static final String SQL_GET_CATEGORY_ID = "SELECT id FROM categories WHERE category = ?";
    private static final String SQL_VALIDATE_USER = "SELECT * from users where username = ? and id = ?";
    static final String SQL_GET_USER_PROFILE_DATA = "SELECT id, password, username, display_name, email, phone FROM users WHERE username = ?";
    static final String SQL_GET_ALL_ADS = """
            SELECT ad.id as ad_id, u.display_name, u.email, u.phone, ad.pet_name, c.category, ad.pet_age, ad.pet_gender, ad.ad_content, ad.image_path, ad.created_at
            FROM users u
            JOIN ads ad ON u.id = ad.author_id
            JOIN categories c on c.id = ad.category_id
            order by ad.id desc
            LIMIT ? OFFSET ?;
            """;
    static final String SQL_GET_ALL_ADS_SPECIFIC_CATEGORIES = """
            SELECT ad.id as ad_id, u.display_name, u.email, u.phone, ad.pet_name, c.category, ad.pet_age, ad.pet_gender, ad.ad_content, ad.image_path, ad.created_at
            FROM users u
            JOIN ads ad ON u.id = ad.author_id
            JOIN categories c on c.id = ad.category_id
            WHERE
            c.category = ?
            order by ad.id desc
            LIMIT ? OFFSET ?;
            """;
    static final String SQL_GET_USER_ADS = """
            SELECT ad.id as ad_id, u.display_name, u.email, u.phone, ad.pet_name, c.category, ad.pet_age, ad.pet_gender, ad.ad_content, ad.image_path, ad.created_at
            FROM users u
            JOIN ads ad ON u.id = ad.author_id
            JOIN categories c on c.id = ad.category_id
            WHERE u.id = ?
            order by ad.id desc
            LIMIT ? OFFSET ?;
            """;
    static final String SQL_GET_USER_FAVORITE_ADS = """
            SELECT ad.id as ad_id, u.display_name, u.email, u.phone, ad.pet_name, c.category, ad.pet_age, ad.pet_gender, ad.ad_content, ad.image_path, ad.created_at
            FROM users u
            JOIN ads ad ON u.id = ad.author_id
            JOIN categories c on c.id = ad.category_id
            JOIN favorites f on f.ad_id = ad.id
            WHERE f.user_id = ?
            order by ad.id desc
            LIMIT ? OFFSET ?;
            """;
    static final String SQL_COUNT_ALL_ADS = "SELECT COUNT(*) as count FROM ads;";
    static final String SQL_COUNT_ALL_ADS_OF_USER = "SELECT COUNT(*) as count FROM ads where author_id=? ;";
    static final String SQL_COUNT_ALL_FAVORITE_ADS_OF_USER =
            """
                    SELECT COUNT(*)
                    FROM ads ad
                    JOIN favorites f on f.ad_id = ad.id
                    WHERE f.user_id = ?
                    """;
    static final String SQL_COUNT_ADS_SPECIFIC_CATEGORIES = "SELECT COUNT(*) as count FROM ads ad JOIN categories c on c.id = ad.category_id WHERE c.category = ?;";

    /**
     * Creates a new user in the database.
     * This method hashes the user's password, validates the email and phone number,
     * and inserts the user details into the database. If the email or phone number
     * are invalid, or if the user could not be created,
     * it throws a RuntimeException.
     *
     * @param username    The username of the new user.
     * @param password    The password of the new user, which will be hashed before storing.
     * @param displayName The display name of the new user.
     * @param email       The email address of the new user, which must be valid.
     * @param phone       The phone number of the new user, which must be valid.
     * @throws DataAccessException if there is an error accessing the database.
     * @throws SQLException        if a database access error occurs.
     */
    public synchronized void createUser(String username, String password, String displayName, String email, String phone) throws DataAccessException, SQLException {
        if (!validEmail(email)) {
            throw new RuntimeException("Invalid email");
        }
        if (!validPhone(phone)) {
            throw new RuntimeException("Invalid phone number");
        }

        String hashedPassword = hashPassword(password);
        int affectedRows = eq.updateDB(RETRY, SQL_CREATE_NEW_USER, username, hashedPassword, displayName, email, phone);
        if (affectedRows < 1) {
            log.warn("User wasn't created");
            throw new RuntimeException("An unexpected error occurred - user should have been created");
        }
    }

    /**
     * Validates the format of an email address.
     * This method checks if the given email address matches the defined pattern,
     * which ensures that the email contains characters before and after the '@' symbol,
     * followed by a dot ('.') and a suffix.
     *
     * @param email The email address to validate.
     * @return true if the email address is valid, false otherwise.
     */
    private boolean validEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates the format of a phone number.
     * This method checks if the given phone number matches the defined pattern,
     * which ensures that the phone number contains only digits and hyphens.
     *
     * @param phone The phone number to validate.
     * @return true if the phone number is valid, false otherwise.
     */
    private boolean validPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Checks if a given username and password match.
     * This method verifies if the username exists and if the provided password matches
     * the stored password for that user.
     *
     * @param username the username trying to log in
     * @param password the password provided by the user
     * @return JSON string of user profile if login is successful, null otherwise
     */
    public String isAllowedToLogin(String username, String password) {
        try {
            val res = eq.queryDB(RETRY, SQL_GET_USER_PROFILE_DATA, username);
            if (res.size() < 1) {
                return null;
            }
            String retrievedPassword = (String) res.get(0).getOrDefault("password", "");
            return checkPassword(password, retrievedPassword) ? gson.toJson(ResultSetMapper.mapRowToRecord(res.get(0), UserProfile.class)) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            log.info("No such user {} error: {}", username, e.getMessage());
            throw new IndexOutOfBoundsException();
        }
    }


    /**
     * Validates if the user is allowed to create an ad based on their username and userID.
     * Queries the database to check if the user exists and is eligible to create an ad.
     *
     * @param userName The username of the user to validate.
     * @param userID   The user ID of the user to validate.
     * @return true if the user is allowed to create an ad, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean isAllowedToCreateAd(String userName, String userID) throws SQLException {
        val affectedRows = eq.queryDB(RETRY, SQL_VALIDATE_USER, userName, Integer.valueOf(userID));
        return affectedRows.size() == 1;
    }


    /**
     * Creates a new ad in the database.
     *
     * @param categoryId the category ID of the ad
     * @param authorId the user ID of the ad author
     * @param petName the name of the pet
     * @param petAge the age of the pet
     * @param petGender the gender of the pet
     * @param adContent the content of the ad
     * @param imagePath the path of the ad image
     * @throws DataAccessException if there is an error accessing the database
     * @throws SQLException if a database access error occurs
     */
    public synchronized void createNewAd(int categoryId, int authorId, String petName, Double petAge, String petGender, String adContent, String imagePath) throws DataAccessException, SQLException {
        int affectedRows = eq.updateDB(RETRY, SQL_CREATE_NEW_AD, categoryId, authorId, petName, petAge, petGender, adContent, imagePath);
        if (affectedRows < 1) {
            log.warn("Ad wasn't created");
            throw new RuntimeException("Ad was not created");
        }
    }

    /**
     * Deletes an ad from the database.
     *
     * @param adId the ID of the ad to be deleted
     * @throws DataAccessException if there is an error accessing the database
     * @throws SQLException if a database access error occurs
     */
    public synchronized void deleteAd(int adId) throws DataAccessException, SQLException {
        int affectedRowsFavoriteTable = eq.updateDB(RETRY, SQL_DELETE_FAVORITE_AD, adId);
        log.info("Deleted {} ads from favorites", affectedRowsFavoriteTable);

        int affectedRowsAdTable = eq.updateDB(RETRY, SQL_DELETE_AD, adId);
        if (affectedRowsAdTable < 1) {
            log.warn("Ad wasn't deleted from ads table");
            throw new RuntimeException("Ad wasn't deleted from ads table");
        }
    }

    /**
     * Deletes an ad from a user's favorites.
     *
     * @param userId the user ID
     * @param adId the ad ID
     * @throws DataAccessException if there is an error accessing the database
     * @throws SQLException if a database access error occurs
     */
    public void deleteAdFromFavorites(int userId, int adId) throws DataAccessException, SQLException {
        int affectedRowsFavoriteTable = eq.updateDB(RETRY, SQL_DELETE_USER_FAVORITE_AD, userId, adId);
        log.info("Deleted {} ads from the favorites of the user", affectedRowsFavoriteTable);
    }

    /**
     * Hashes a password using the SCrypt algorithm with predefined settings.
     *
     * @param password The plaintext password to be hashed.
     * @return A string representing the hashed password.
     */
    static String hashPassword(String password) {
        return SCryptUtil.scrypt(password, 16384, 8, 1);
    }

    /**
     * Checks if a plaintext password matches a hashed one using SCrypt.
     *
     * @param password The plaintext password to verify.
     * @param hashed   The previously hashed password for comparison.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String password, String hashed) {
        return SCryptUtil.check(password, hashed);
    }

    /**
     * Fetches general ads with pagination and optional category filtering.
     *
     * @param page the page number
     * @param limit the number of ads per page
     * @param category the category to filter by (optional)
     * @return a list of ads
     */
    public List<AdDetail> getGeneralAds(int page, int limit, String category) {
        List<AdDetail> ads = new ArrayList<>();
        List<Map<String, Object>> results;
        int offset = (page - 1) * limit;
        try {
            if (category != null && !category.isEmpty()) {
                results = eq.queryDB(RETRY, SQL_GET_ALL_ADS_SPECIFIC_CATEGORIES, category, limit, offset);
            } else {
                results = eq.queryDB(RETRY, SQL_GET_ALL_ADS, limit, offset);
            }
            return adsList(results);
        } catch (SQLException e) {
            log.error("Error fetching ads: {}", e.getMessage());
        }
        return ads;
    }

    /**
     * Gets the total count of ads, optionally filtered by category.
     *
     * @param category the category to filter by (optional)
     * @return the total number of ads
     */
    public long getTotalAdsCount(String category) {
        try {
            if (category != null && !category.isEmpty()) {
                return (Long) eq.queryDB(RETRY, SQL_COUNT_ADS_SPECIFIC_CATEGORIES, category).get(0).get("count");
            } else {
                return (Long) eq.queryDB(RETRY, SQL_COUNT_ALL_ADS).get(0).get("count");
            }
        } catch (SQLException e) {
            log.error("Error fetching ads: {}", e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            log.error("No ads in DB");
        }
        return 0;
    }

    /**
     * Gets the total count of ads created by a specific user.
     *
     * @param userId the user ID
     * @return the total number of user ads
     */
    public long getTotalUserAdsCount(int userId) {
        try {
            return (Long) eq.queryDB(RETRY, SQL_COUNT_ALL_ADS_OF_USER, userId).get(0).getOrDefault("count", 0);
        } catch (SQLException e) {
            log.error("Error fetching ads: {}", e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            log.error("No ads in DB");
        }
        return 0;
    }

    /**
     * Gets the total count of favorite ads of a specific user.
     *
     * @param userId the user ID
     * @return the total number of user favorite ads
     */
    public long getTotalUserFavoriteAdsCount(int userId) {
        try {
            return (Long) eq.queryDB(RETRY, SQL_COUNT_ALL_FAVORITE_ADS_OF_USER, userId).get(0).getOrDefault("count", 0);
        } catch (SQLException e) {
            log.error("Error fetching ads: {}", e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            log.error("No ads in DB");
        }
        return 0;
    }

    /**
     * Fetches ads created by a specific user with pagination.
     *
     * @param userId the user ID
     * @param page the page number
     * @param limit the number of ads per page
     * @return a list of user ads
     */
    public List<AdDetail> getUserAds(int userId, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<Map<String, Object>> results = eq.queryDB(RETRY, SQL_GET_USER_ADS, userId, limit, offset);
            return adsList(results);
        } catch (SQLException e) {
            log.error("Error fetching user ads: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Fetches favorite ads of a specific user with pagination.
     *
     * @param userId the user ID
     * @param page the page number
     * @param limit the number of ads per page
     * @return a list of user favorite ads
     */
    public List<AdDetail> getUserFavoritesAds(int userId, int page, int limit) {
        try {
            int offset = (page - 1) * limit;
            List<Map<String, Object>> results = eq.queryDB(RETRY, SQL_GET_USER_FAVORITE_ADS, userId, limit, offset);
            return adsList(results);
        } catch (SQLException e) {
            log.error("Error fetching user ads: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Converts a list of maps (query results) to a list of AdDetail records.
     *
     * @param queryRes the query results as a list of maps
     * @return a list of AdDetail records
     */
    List<AdDetail> adsList(List<Map<String, Object>> queryRes) {
        List<AdDetail> ads;
        ads = queryRes.stream()
                .map(row -> {
                    try {
                        return ResultSetMapper.mapRowToRecord(row, AdDetail.class);
                    } catch (SQLException e) {
                        log.error("Error mapping row to AdDetail: {}", e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull) // Filter out any null results due to mapping errors
                .toList();
        return ads;
    }

    /**
     * Fetches the category ID for a given category name.
     *
     * @param category the displayed name of the chosen category
     * @return the category ID, or 1 if the category does not exist
     */
    public int findCategoryIdByName(String category) {
        try {
            val res = eq.queryDB(RETRY, SQL_GET_CATEGORY_ID, category);
            return (Integer) res.get(0).getOrDefault("id", 1);
        } catch (SQLException e) {
            log.info(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            log.error("No such category {} error: {}", category, e.getMessage());
        }
        return 1; // Default category
    }

    /**
     * Inserts an ad into the user's favorites.
     *
     * @param authorId the user ID
     * @param adId the ad ID
     * @throws SQLException if a database access error occurs
     */
    public synchronized void insertAdsToFavorites(int authorId, int adId) throws SQLException {
        int affectedRows = eq.updateDB(RETRY, SQL_CREATE_NEW_FAVORITE_AD, authorId, adId);
        log.info("Inserted {} ads to favorites for user id {}", affectedRows, authorId);
    }
}
