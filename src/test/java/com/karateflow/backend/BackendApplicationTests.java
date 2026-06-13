package com.karateflow.backend;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;


class BackendApplicationTests extends BaseIntegrationTest {

    @Container
    static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:7.0");

    @Test
    void contextLoads() {}
}