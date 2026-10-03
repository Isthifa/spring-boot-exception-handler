package io.github.isthifa.exceptionhandler.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the exception-handler auto-configuration.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
@ConfigurationProperties("generic.exception-handler")
public class ExceptionHandlerProperties {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
