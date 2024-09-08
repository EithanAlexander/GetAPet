package com.example.utils;

import java.lang.reflect.RecordComponent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * Utility class for mapping a {@link ResultSet} to a Java record.
 */

public class ResultSetMapper {

    private ResultSetMapper() {
    }

    /**
     * Maps a {@link Map} representing a database row to a record of the specified type.
     *
     * @param row         the {@link Map} representing a database row
     * @param recordClass the class of the record to map to
     * @param <T>         the type of the record
     * @return an instance of the specified record type populated with data from the {@link Map}
     * @throws SQLException if an error occurs while mapping the row to the record
     */
    public static <T extends Record> T mapRowToRecord(Map<String, Object> row, Class<T> recordClass) throws SQLException {
        try {
            // Get the fields of the record class
            RecordComponent[] fields = recordClass.getRecordComponents();
            Object[] values = new Object[fields.length];

            // Loop through each component to retrieve values from the Map
            for (int i = 0; i < fields.length; i++) {
                String columnName = fields[i].getName(); // Get the column name which is the same as the component name
                Class<?> type = fields[i].getType(); // Get the type of the component
                // Map the Map value to the component type
                Object value = row.get(columnName);
                if (type == LocalDateTime.class) {
                    values[i] = value != null ? ((Timestamp) value).toLocalDateTime() : null;
                } else {
                    values[i] = value;
                }
            }
            // Create a new instance of the record using the retrieved values
            return recordClass.getDeclaredConstructor(
                    Arrays.stream(fields).map(RecordComponent::getType).toArray(Class[]::new)
            ).newInstance(values);
        } catch (Exception e) {
            // Throw a SQLException if any error occurs during the mapping
            throw new SQLException("Failed to map row to record: " + e.getMessage(), e);
        }
    }
}
