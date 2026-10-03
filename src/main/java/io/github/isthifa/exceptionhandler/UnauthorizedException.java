package io.github.isthifa.exceptionhandler;

import org.springframework.http.HttpStatus;

/**
 * Signals that a request requires authentication.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public class UnauthorizedException extends ApiException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
