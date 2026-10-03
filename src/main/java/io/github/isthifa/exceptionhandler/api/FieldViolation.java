package io.github.isthifa.exceptionhandler.api;

/**
 * Describes one rejected input field or request parameter.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
public record FieldViolation(String field, Object rejectedValue, String message) {
}
