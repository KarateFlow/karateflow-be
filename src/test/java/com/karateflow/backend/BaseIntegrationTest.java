package com.karateflow.backend;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
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
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Autowired
    private MongoTemplate mongoTemplate; // Iniettiamo il template per manipolare il DB di test

    @BeforeEach
    void cleanDatabase() {
        // Recupera i nomi di tutte le collezioni esistenti nel Mongo temporaneo
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            // Escludiamo le collezioni di sistema interne di MongoDB
            if (!collectionName.startsWith("system.")) {
                // Svuota la collezione eliminando tutti i documenti, preservando gli indici
                mongoTemplate.getCollection(collectionName).deleteMany(new Document());
            }
        }
    }
}