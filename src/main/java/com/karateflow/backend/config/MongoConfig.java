package com.karateflow.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@Profile("!test")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "karateflow_db";
    }

    // AGGIUNGI QUESTO METODO
    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString("mongodb://admin:password@localhost:27017/karateflow_db?authSource=admin");
        final MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        return MongoClients.create(settings);
    }
}