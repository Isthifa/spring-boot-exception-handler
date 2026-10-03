package io.github.isthifa.exceptionhandler;

import org.springframework.http.HttpStatus;

/**
 * Signals that a client sent an invalid request.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public class BadRequestException extends ApiException {

    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
