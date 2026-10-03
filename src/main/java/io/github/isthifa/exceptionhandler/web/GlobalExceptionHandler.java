package io.github.isthifa.exceptionhandler.web;

import io.github.isthifa.exceptionhandler.ApiException;
import io.github.isthifa.exceptionhandler.api.ApiErrorResponse;
import io.github.isthifa.exceptionhandler.api.FieldViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

/**
 * Converts common Spring MVC and application exceptions to a consistent JSON error response.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, exception.getParameterName() + " parameter is required");
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return response(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type: " + exception.getContentType());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldViolation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                .toList();
        return response(HttpStatus.BAD_REQUEST, "Validation failed", null, violations);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiException(ApiException exception) {
        return response(exception.getStatus(), exception.getMessage(), exception.getErrorCode(), List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception) {
        List<FieldViolation> violations = exception.getConstraintViolations().stream()
                .map(violation -> new FieldViolation(violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()))
                .toList();
        return response(HttpStatus.BAD_REQUEST, "Validation failed", null, violations);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String requiredType = exception.getRequiredType() == null ? "the required type" : exception.getRequiredType().getSimpleName();
        String message = "Parameter '%s' could not be converted to %s".formatted(exception.getName(), requiredType);
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        LOGGER.warn("Database integrity violation", exception);
        return response(HttpStatus.CONFLICT, "The request conflicts with existing data");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleUnexpectedException(Exception exception) {
        LOGGER.error("Unhandled exception", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Object> response(HttpStatusCode status, String message) {
        return response(status, message, null, List.of());
    }

    private ResponseEntity<Object> response(
            HttpStatusCode status, String message, String errorCode, List<FieldViolation> violations) {
        HttpStatus resolvedStatus = HttpStatus.resolve(status.value());
        String error = resolvedStatus == null ? "HTTP " + status.value() : resolvedStatus.getReasonPhrase();
        ApiErrorResponse body = new ApiErrorResponse(Instant.now(), status.value(), error, message, errorCode, violations);
        return ResponseEntity.status(status).<Object>body(body);
    }
}
