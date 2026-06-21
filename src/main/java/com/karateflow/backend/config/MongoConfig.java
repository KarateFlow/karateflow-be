package com.karateflow.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@Profile("!test")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database:karateflow_db}")
    private String databaseName;

    @Value("${spring.data.mongodb.uri:}")
    private String mongoUri;

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @Value("${spring.data.mongodb.port:27017}")
    private int port;

    @Value("${spring.data.mongodb.username:}")
    private String username;

    @Value("${spring.data.mongodb.password:}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database:admin}")
    private String authSource;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    public MongoClient mongoClient() {
        String uriToUse = mongoUri;
        if (uriToUse == null || uriToUse.isEmpty()) {
            if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                uriToUse = String.format("mongodb://%s:%s@%s:%d/%s?authSource=%s", 
                        username, password, host, port, databaseName, authSource);
            } else {
                uriToUse = String.format("mongodb://%s:%d/%s", host, port, databaseName);
            }
        }
        
        final ConnectionString connectionString = new ConnectionString(uriToUse);
        final MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        return MongoClients.create(settings);
    }
}