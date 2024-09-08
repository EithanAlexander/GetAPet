package com.example.utils;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResultSetMapperTest {

    public record TestRecord(int id, String name, LocalDateTime created_at) {}

    /**
     * Test to verify that a Map with valid data is correctly mapped to the TestRecord record.
     */
    @Test
    void testMapRowToRecord() throws SQLException {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> testVal = Map.of(
                "id", 1,
                "name", "Test Name",
                "created_at", Timestamp.valueOf(now)
        );
        // Act
        TestRecord result = ResultSetMapper.mapRowToRecord(testVal, TestRecord.class);
        // Assert
        assertEquals(1, result.id());
        assertEquals("Test Name", result.name());
        assertEquals(now, result.created_at());
    }

    /**
     * Test to verify that an exception is thrown when the map contains an invalid type for one of the fields.
     */
    @Test
    void testMapRowToRecordWithInvalidType() {
        // Arrange
        Map<String, Object> testVal = Map.of(
                "id", "Invalid Type", // This should be an int
                "name", "Test Name",
                "created_at", Timestamp.valueOf(LocalDateTime.now())
        );
        // Act & Assert
        assertThrows(SQLException.class, () -> {
            ResultSetMapper.mapRowToRecord(testVal, TestRecord.class);
        });
    }

    /**
     * Test to verify that the mapper ignores extra fields not present in the record.
     */
    @Test
    void testMapRowToRecordWithExtraFields() throws SQLException {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> testVal = Map.of(
                "id", 1,
                "name", "Test Name",
                "created_at", Timestamp.valueOf(now),
                "extra_field", "Extra Value"
        );
        // Act
        TestRecord result = ResultSetMapper.mapRowToRecord(testVal, TestRecord.class);
        // Assert
        assertEquals(1, result.id());
        assertEquals("Test Name", result.name());
        assertEquals(now, result.created_at());
    }

    /**
     * Test to verify that an exception is thrown when the map is missing required fields.
     */
    @Test
    void testMapRowToRecordWithMissingFields() {
        // Arrange
        Map<String, Object> testVal = Map.of(
                "id", 1,
                "name", "Test Name",
                "created_at", ""
        );
        // Act & Assert
        assertThrows(SQLException.class, () -> {
            ResultSetMapper.mapRowToRecord(testVal, TestRecord.class);
        });
    }
}
