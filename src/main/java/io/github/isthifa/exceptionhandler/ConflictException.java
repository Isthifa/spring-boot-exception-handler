package io.github.isthifa.exceptionhandler;

import org.springframework.http.HttpStatus;

/**
 * Signals that a request conflicts with the current resource state.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public class ConflictException extends ApiException {

    private static final long serialVersionUID = 1L;

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
