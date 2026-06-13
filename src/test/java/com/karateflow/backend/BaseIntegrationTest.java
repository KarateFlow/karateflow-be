package com.karateflow.backend;

import org.junit.jupiter.api.BeforeEach;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
@ActiveProfiles("test")
// RIMOSSO @Testcontainers: ci pensiamo noi manualmente a livello di JVM
public abstract class BaseIntegrationTest {

    // RIMOSSO @Container: gestiamo il ciclo di vita come Singleton statico puro
    protected static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:7.0");

    static {
        // Questo blocco viene eseguito UNA SOLA VOLTA all'avvio assoluto dei test
        mongoContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // Le chiavi di Spring Boot 4.x che abbiamo visto funzionare perfettamente
        registry.add("spring.mongodb.uri", mongoContainer::getReplicaSetUrl);
        registry.add("spring.mongodb.host", mongoContainer::getHost);
        registry.add("spring.mongodb.port", mongoContainer::getFirstMappedPort);
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