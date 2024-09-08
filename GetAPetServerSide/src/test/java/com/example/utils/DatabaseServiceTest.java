package com.example.utils;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DatabaseServiceTest {

    @Mock
    private ExecuteQuery executeQuery;

    @InjectMocks
    private DatabaseService databaseService;
    private static final Map<String, Object> USER_RESULT_MAP = Map.of(
            "id", 1,
            "username", "userName",
            "password", "password",
            "displayName", "Test User",
            "email", "testuser@example.com",
            "phone", "1234567890"
    );
    private static final List<Map<String, Object>> RESULT_LIST = List.of(USER_RESULT_MAP);
    public static final Map<String, Object> AD_DETAILS = Map.ofEntries(Map.entry("ad_id", 1),
            Map.entry("display_name", "Test User"),
            Map.entry("email", "testuser@example.com"),
            Map.entry("phone", "1234567890"),
            Map.entry("pet_name", "Buddy"),
            Map.entry("category", "Dogs"),
            Map.entry("pet_age", 2),
            Map.entry("pet_gender", "Male"),
            Map.entry("ad_content", "Adorable puppy"),
            Map.entry("image_path", "/images/buddy.jpg"),
            Map.entry("created_at", Timestamp.valueOf(LocalDateTime.now())));


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() throws DataAccessException, SQLException {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        String displayName = "Test User";
        String email = "testuser@example.com";
        String phone = "1234567890";
        when(executeQuery.updateDB(anyInt(), eq(DatabaseService.SQL_CREATE_NEW_USER), any(), any(), any(), any(), any()))
                .thenReturn(1);
        // Act
        databaseService.createUser(username, password, displayName, email, phone);
        // Assert
        verify(executeQuery, times(1)).updateDB(anyInt(), eq(DatabaseService.SQL_CREATE_NEW_USER), eq(username), anyString(), eq(displayName), eq(email), eq(phone));
    }

    @Test
    void testCreateNewAd() throws DataAccessException, SQLException {
        // Arrange
        int categoryId = 1;
        int authorId = 1;
        String petName = "Buddy";
        Double petAge = 2.0;
        String petGender = "Male";
        String adContent = "Adorable puppy";
        String imagePath = "/images/buddy.jpg";
        when(executeQuery.updateDB(anyInt(), eq(DatabaseService.SQL_CREATE_NEW_AD), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);
        // Act
        databaseService.createNewAd(categoryId, authorId, petName, petAge, petGender, adContent, imagePath);
        // Assert
        verify(executeQuery, times(1)).updateDB(anyInt(), eq(DatabaseService.SQL_CREATE_NEW_AD), eq(categoryId), eq(authorId), eq(petName), eq(petAge), eq(petGender), eq(adContent), eq(imagePath));
    }

    @Test
    void testDeleteAd() throws DataAccessException, SQLException {
        // Arrange
        int adId = 1;
        when(executeQuery.updateDB(anyInt(), eq(DatabaseService.SQL_DELETE_FAVORITE_AD), anyInt()))
                .thenReturn(1);
        when(executeQuery.updateDB(anyInt(), eq(DatabaseService.SQL_DELETE_AD), anyInt()))
                .thenReturn(1);
        // Act
        databaseService.deleteAd(adId);
        // Assert
        verify(executeQuery, times(1)).updateDB(anyInt(), eq(DatabaseService.SQL_DELETE_FAVORITE_AD), eq(adId));
        verify(executeQuery, times(1)).updateDB(anyInt(), eq(DatabaseService.SQL_DELETE_AD), eq(adId));
    }

    @Test
    void testDeleteAdFromFavorites() throws DataAccessException, SQLException {
        // Arrange
        int userId = 1;
        int adId = 1;
        when(executeQuery.updateDB(anyInt(), eq(DatabaseService.SQL_DELETE_USER_FAVORITE_AD), anyInt(), anyInt()))
                .thenReturn(1);
        // Act
        databaseService.deleteAdFromFavorites(userId, adId);
        // Assert
        verify(executeQuery, times(1)).updateDB(anyInt(), eq(DatabaseService.SQL_DELETE_USER_FAVORITE_AD), eq(userId), eq(adId));
    }

    @Test
    void testGetGeneralAds() throws SQLException {
        // Arrange
        int page = 1;
        int limit = 10;
        String category = "";
        List<Map<String, Object>> mockResults = new ArrayList<>();
        mockResults.add(AD_DETAILS);
        when(executeQuery.queryDB(anyInt(), eq(DatabaseService.SQL_GET_ALL_ADS), anyInt(), anyInt()))
                .thenReturn(mockResults);
        // Act
        val result = databaseService.getGeneralAds(page, limit, category);
        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Buddy", result.get(0).pet_name());
    }

    @Test
    void testGetTotalAdsCount() throws SQLException {
        // Arrange
        String category = "";
        Map<String, Object> mockResult = Map.of("count", 5L);
        when(executeQuery.queryDB(anyInt(), eq(DatabaseService.SQL_COUNT_ALL_ADS)))
                .thenReturn(List.of(mockResult));
        // Act
        long result = databaseService.getTotalAdsCount(category);
        // Assert
        assertEquals(5L, result);
    }

    @Test
    void testGetUserAds() throws SQLException {
        // Arrange
        int userId = 1;
        List<Map<String, Object>> mockResults = new ArrayList<>();
        mockResults.add(AD_DETAILS);
        when(executeQuery.queryDB(anyInt(), eq(DatabaseService.SQL_GET_USER_ADS), anyInt(),anyInt(),anyInt()))
                .thenReturn(mockResults);
        // Act
        List<DatabaseService.AdDetail> result = databaseService.getUserAds(userId,1,1);
        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Buddy", result.get(0).pet_name());
    }

    @Test
    void testGetUserFavoritesAds() throws SQLException {
        // Arrange
        int userId = 1;
        List<Map<String, Object>> mockResults = new ArrayList<>();
        mockResults.add(AD_DETAILS);
        when(executeQuery.queryDB(anyInt(), eq(DatabaseService.SQL_GET_USER_FAVORITE_ADS), anyInt(),anyInt(),anyInt()))
                .thenReturn(mockResults);
        // Act
        List<DatabaseService.AdDetail> result = databaseService.getUserFavoritesAds(userId,1,1);
        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Buddy", result.get(0).pet_name());
    }

    @Test
    void testFindCategoryIdByName() throws SQLException {
        // Arrange
        String category = "Dogs";
        Map<String, Object> mockResult = Map.of("id", 1);
        when(executeQuery.queryDB(anyInt(), eq(DatabaseService.SQL_GET_CATEGORY_ID), anyString()))
                .thenReturn(List.of(mockResult));
        // Act
        int result = databaseService.findCategoryIdByName(category);
        // Assert
        assertEquals(1, result);
    }

    @Test
    void testInsertAdsToFavorites() throws SQLException {
        // Arrange
        int authorId = 1;
        int adId = 1;
        when(executeQuery.updateDB(anyInt(), eq(DatabaseService.SQL_CREATE_NEW_FAVORITE_AD), anyInt(), anyInt()))
                .thenReturn(1);
        // Act
        databaseService.insertAdsToFavorites(authorId, adId);
        // Assert
        verify(executeQuery, times(1)).updateDB(anyInt(), eq(DatabaseService.SQL_CREATE_NEW_FAVORITE_AD), eq(authorId), eq(adId));
    }
}
