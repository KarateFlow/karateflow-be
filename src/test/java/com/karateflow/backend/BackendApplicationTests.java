package com.karateflow.backend;

import org.junit.jupiter.api.Test;


class BackendApplicationTests extends BaseIntegrationTest {

    @Test
    void contextLoads() {
        // Arrange
        // Act (Context loading handled by Spring Extension)
        
        // Assert
        org.hamcrest.MatcherAssert.assertThat(mongoContainer.isRunning(), org.hamcrest.Matchers.is(true));
    }
}