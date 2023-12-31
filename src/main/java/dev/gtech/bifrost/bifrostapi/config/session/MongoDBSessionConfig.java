package dev.gtech.bifrost.bifrostapi.config.session;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.mongo.JdkMongoSessionConverter;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

@Configuration
@EnableMongoHttpSession
public class MongoDBSessionConfig {
    
    @Bean
    public JdkMongoSessionConverter jdkMongoSessionConverter() {
        return new JdkMongoSessionConverter(Duration.ofHours(20));
    }
}
