package io.github.isthifa.exceptionhandler;

import org.springframework.http.HttpStatusCode;

/**
 * Base exception for failures that are safe to return from a REST API.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatusCode status;
    private final String errorCode;

    public ApiException(HttpStatusCode status, String message) {
        this(status, message, null);
    }

    public ApiException(HttpStatusCode status, String message, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
