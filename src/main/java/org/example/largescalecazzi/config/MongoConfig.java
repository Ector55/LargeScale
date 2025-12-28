package org.example.largescalecazzi.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {
    @Value("${spring.mongodb.uri}")
    private String uri;
    private MongoClient mongoIstance;

    @Bean
    public MongoClient getClient(){
        if(mongoIstance == null){
            ConnectionString connectionString = new ConnectionString(uri);

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(5, TimeUnit.SECONDS))
                    .applyToConnectionPoolSettings(builder ->
                            builder.maxConnecting(2))
                    .applyToConnectionPoolSettings(builder ->
                            builder.maxSize(50))
                    .retryWrites(true)
                    .writeConcern(WriteConcern.MAJORITY)
                    .build();

            mongoIstance = MongoClients.create(settings);
        }

        return mongoIstance;
    }
}
