package io.github.isthifa.exceptionhandler.api;

import java.time.Instant;
import java.util.List;

/**
 * Standard error body returned by the global exception handler.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String errorCode,
        List<FieldViolation> violations) {
}
