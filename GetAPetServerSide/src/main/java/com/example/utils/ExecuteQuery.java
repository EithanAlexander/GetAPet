package com.example.utils;

import org.apache.commons.dbcp2.BasicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service class for managing database query execution with robust error handling and retry logic.
 * This class supports executing both query and update operations on a database,
 * utilizing a retry mechanism to handle transient and recoverable SQL exceptions.
 * Uses Apache DBCP for connection pooling and SLF4J for logging.
 * JDBC is used directly for executing SQL queries and updates, providing precise control over database interactions.
 */
@Service
@Slf4j
public class ExecuteQuery {

    private final BasicDataSource dataSource;
    private static final Random rand = new Random();
    private static final int INITIAL_SLEEP_DURATION_MS = 1000;
    private static final int MAX_RETRIES = 3;

    /**
     * Constructor to initialize the data source.
     *
     * @param dataSource The data source for database connections.
     */
    @Autowired
    public ExecuteQuery(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Functional interface for database operations.
     *
     * @param <T> The type of the result of the operation.
     */
    private interface DBOperation<T> {
        T execute(Connection con, PreparedStatement preparedStatement) throws SQLException;
    }

    /**
     * Executes a database query with a retry mechanism.
     *
     * @param retry     The current retry attempt.
     * @param query     The SQL query to execute.
     * @param params    The parameters for the SQL query.
     * @param operation The operation to perform on the database.
     * @param <T>       The type of the result of the operation.
     * @return The result of the database operation.
     * @throws SQLException if a database access error occurs.
     */
    private <T> T executeQuery(int retry, String query, Object[] params, DBOperation<T> operation) throws SQLException {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            preparedStatement = con.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            T result = operation.execute(con, preparedStatement);
            con.commit();
            return result;
        } catch (DuplicateKeyException e) {
            log.info(e.getMessage());
            throw e;
        } catch (SQLRecoverableException | SQLTransientException e) {
            log.warn("Recoverable exception occurred, retry attempt: {}", retry, e);
            if (shouldRetry(retry)) {
                sleepUntilNextTry(retry);
                return executeQuery(retry + 1, query, params, operation);
            }
            throw new SQLException(e);
        } catch (PSQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new PSQLException(e.getServerErrorMessage() != null ? e.getServerErrorMessage() : new ServerErrorMessage("Key duplication"));
            }
            throw new SQLException(e);
        } catch (SQLException e) {
            log.error("Non-recoverable SQL exception occurred, attempting rollback", e);
            handleSQLException(con);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new SQLException(e);
        } finally {
            handleClose(preparedStatement, con);
        }
    }

    /**
     * Closes the SQL statement and the database connection.
     *
     * @param preparedStatement The SQL statement used.
     * @param con               The connection to the database.
     */
    private void handleClose(PreparedStatement preparedStatement, Connection con) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("Failed to close PreparedStatement", e);
            }
        }
        if (con != null) {
            try {
                con.setAutoCommit(true);
                con.close();
            } catch (SQLException e) {
                log.error("Failed to restore auto-commit or close connection", e);
            }
        }
    }

    /**
     * Handles SQL exceptions by rolling back the transaction.
     *
     * @param con The connection to the database.
     * @throws SQLException if a rollback or auto-commit restoration fails.
     */
    private void handleSQLException(Connection con) throws SQLException {
        if (con != null) {
            try {
                con.setAutoCommit(false);
                con.rollback();
                log.info("Transaction rolled back successfully");
            } catch (SQLException rollbackException) {
                log.error("Rollback failed", rollbackException);
                throw new SQLException("Rollback failed: " + rollbackException.getMessage(), rollbackException);
            }
        }
    }

    /**
     * Waits for a duration based on the retry count using exponential backoff.
     *
     * @param retryCount The current retry attempt count.
     */
    private void sleepUntilNextTry(int retryCount) {
        int randRetry = rand.nextInt(retryCount * 1000);
        int sleepDuration = (int) (INITIAL_SLEEP_DURATION_MS * Math.pow(2, randRetry));
        try {
            log.info("On retry {} Thread {} will now sleep for {} ms", retryCount, Thread.currentThread().getName(), sleepDuration);
            TimeUnit.MILLISECONDS.sleep(sleepDuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrupted during sleep", e);
            throw new RuntimeException();
        }
    }

    /**
     * Determines if a retry should be attempted.
     *
     * @param retryCount The current retry attempt count.
     * @return true if a retry should be attempted, false otherwise.
     */
    private boolean shouldRetry(int retryCount) {
        return retryCount < MAX_RETRIES;
    }

    /**
     * Executes a database query and returns the result as a list of maps.
     *
     * @param retry  The current retry attempt.
     * @param query  The SQL query to execute.
     * @param params The parameters for the SQL query.
     * @return The result of the query as a list of maps.
     * @throws SQLException if a database access error occurs.
     */
    public List<Map<String, Object>> queryDB(int retry, String query, Object... params) throws SQLException {
        return executeQuery(retry, query, params, (con, preparedStatement) -> {
            List<Map<String, Object>> results = new ArrayList<>();
            try (ResultSet rs = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
            return results;
        });
    }

    /**
     * Executes a database update (insert, update, delete) and returns the number of affected rows.
     *
     * @param retry  The current retry attempt.
     * @param query  The SQL query to execute.
     * @param params The parameters for the SQL query.
     * @return The number of affected rows.
     * @throws SQLException if a database access error occurs.
     */
    public int updateDB(int retry, String query, Object... params) throws SQLException {
        return executeQuery(retry, query, params, (con, preparedStatement) -> {
            int affectedRows = preparedStatement.executeUpdate();
            log.info("Update executed successfully, affected rows: {}", affectedRows);
            return affectedRows;
        });
    }

}
