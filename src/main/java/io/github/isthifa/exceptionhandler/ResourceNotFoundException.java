package io.github.isthifa.exceptionhandler;

import org.springframework.http.HttpStatus;

/**
 * Signals that a requested resource does not exist.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public class ResourceNotFoundException extends ApiException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
