# Generic Spring Exception Handler

Reusable exception handling for Spring Boot REST APIs.

Author: Isthifa
Created: 2026-10-02

## Build the JAR

```powershell
mvn clean install
```

Maven builds the reusable artifact at `target/generic-exception-handler-1.0.0-SNAPSHOT.jar`; this project also keeps the distributable copy at `dist/generic-exception-handler-1.0.0-SNAPSHOT.jar`. The artifact is installed in your local Maven repository.

## Use from another Spring Boot project

Add this dependency after installing or publishing the JAR:

```xml
<dependency>
    <groupId>io.github.isthifa</groupId>
    <artifactId>generic-exception-handler</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Spring Boot discovers the handler automatically. It produces a consistent response with `timestamp`, `status`, `error`, `message`, and optional `violations` fields.

```java
throw new ResourceNotFoundException("Order 42 was not found");
throw new BadRequestException("The supplied date is invalid");
throw new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Order cannot be cancelled", "ORDER_STATE_INVALID");
```

Built-in mappings include request validation, malformed JSON, invalid request parameters, unsupported media types, database integrity violations, custom API exceptions, and unexpected failures. Unexpected failures are logged but do not expose internal details to clients.

## Configuration

Disable the auto-configured advice in an application that supplies its own handler:

```properties
generic.exception-handler.enabled=false
```

## How it works

This library is a Spring Boot auto-configuration module. When it is present on the classpath of a servlet-based Spring Boot application, Spring Boot reads the following file inside the JAR:

```text
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

That file registers `io.github.isthifa.exceptionhandler.autoconfigure.ExceptionHandlerAutoConfiguration`. The auto-configuration creates the library's `GlobalExceptionHandler` only when all of these conditions are true:

1. The application is a servlet-based Spring Boot web application.
2. `generic.exception-handler.enabled` is either not configured or is `true`.
3. The application has not already registered this library's `GlobalExceptionHandler` bean.

`GlobalExceptionHandler` is a `@RestControllerAdvice`. It intercepts exceptions raised by controllers and returns a predictable JSON error response instead of Spring Boot's default error body.

## Throwing application exceptions

Import and throw the most suitable exception from your service or controller code:

```java
import io.github.isthifa.exceptionhandler.BadRequestException;
import io.github.isthifa.exceptionhandler.ConflictException;
import io.github.isthifa.exceptionhandler.ForbiddenException;
import io.github.isthifa.exceptionhandler.ResourceNotFoundException;
import io.github.isthifa.exceptionhandler.UnauthorizedException;

if (customer == null) {
    throw new ResourceNotFoundException("Customer " + customerId + " was not found");
}

if (emailAlreadyExists) {
    throw new ConflictException("An account already uses this email address");
}
```

Use `ApiException` when the predefined exception types do not fit and you need a custom HTTP status or an application error code:

```java
import io.github.isthifa.exceptionhandler.ApiException;
import org.springframework.http.HttpStatus;

throw new ApiException(
        HttpStatus.UNPROCESSABLE_ENTITY,
        "Order cannot be cancelled after dispatch",
        "ORDER_STATE_INVALID");
```

## Error response format

For `ResourceNotFoundException`, the client receives HTTP `404 Not Found` with a response similar to:

```json
{
  "timestamp": "2026-10-02T17:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Customer 42 was not found",
  "errorCode": null,
  "violations": []
}
```

For request validation failures, such as `@Valid` DTO validation, `violations` contains the field name, rejected value, and validation message. Malformed JSON and unsupported request types are returned as HTTP `400`; database integrity violations are returned as HTTP `409`; unexpected exceptions are logged and returned as a safe HTTP `500` response.

## Using your own exception advice

If your application has a separate `@RestControllerAdvice` that should handle all errors, disable this library before startup:

```properties
generic.exception-handler.enabled=false
```

This disables only the auto-configured handler. The exception classes such as `ResourceNotFoundException` and `ApiException` remain available for your application to use.
