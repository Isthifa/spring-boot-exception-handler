package io.github.isthifa.exceptionhandler.web;

import io.github.isthifa.exceptionhandler.ResourceNotFoundException;
import io.github.isthifa.exceptionhandler.autoconfigure.ExceptionHandlerAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies that the library's auto-configuration returns the standard REST error body.
 *
 * @author Isthifa
 * @since 2026-10-02
 */
@WebMvcTest(GlobalExceptionHandlerIntegrationTest.SampleController.class)
@ContextConfiguration(classes = GlobalExceptionHandlerIntegrationTest.SampleController.class)
@ImportAutoConfiguration(ExceptionHandlerAutoConfiguration.class)
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void mapsResourceNotFoundExceptionToStandardResponse() throws Exception {
        mockMvc.perform(get("/sample"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Sample resource was not found"));
    }

    @RestController
    static class SampleController {

        @GetMapping("/sample")
        String sample() {
            throw new ResourceNotFoundException("Sample resource was not found");
        }
    }
}
