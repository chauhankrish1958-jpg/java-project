package com.protrack.exception;

/**
 * Unchecked exception indicating an underlying JDBC or database access failure.
 */
public class DatabaseOperationException extends RuntimeException {
    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
