package com.karateflow.backend;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;


class BackendApplicationTests extends BaseIntegrationTest {

    @Container
    static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:7.0");

    @Test
    void contextLoads() {
        // Arrange
        // Act (Context loading handled by Spring Extension)
        
        // Assert
        org.hamcrest.MatcherAssert.assertThat(mongoContainer.isRunning(), org.hamcrest.Matchers.is(true));
    }
}