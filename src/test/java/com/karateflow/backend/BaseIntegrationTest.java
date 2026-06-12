package com.karateflow.backend;

import org.junit.jupiter.api.BeforeEach;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    protected static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // 1. Iniettiamo l'URI dinamico con la porta corretta assegnata da Testcontainers
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
        
        // 2. BLINDARE IL CONTESTO: Sovrascriviamo e azzeriamo i vecchi parametri statici
        // Questo impedisce a Spring Boot di cercare "localhost:27017" o l'utente "admin"
        registry.add("spring.data.mongodb.host", () -> null);
        registry.add("spring.data.mongodb.port", () -> null);
        registry.add("spring.data.mongodb.username", () -> null);
        registry.add("spring.data.mongodb.password", () -> null);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void cleanDatabase() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            if (!collectionName.startsWith("system.")) {
                mongoTemplate.getCollection(collectionName).deleteMany(new Document());
            }
        }
    }
}