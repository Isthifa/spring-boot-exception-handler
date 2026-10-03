package io.github.isthifa.exceptionhandler;

import org.springframework.http.HttpStatus;

/**
 * Signals that the current caller lacks permission for an operation.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public class ForbiddenException extends ApiException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
