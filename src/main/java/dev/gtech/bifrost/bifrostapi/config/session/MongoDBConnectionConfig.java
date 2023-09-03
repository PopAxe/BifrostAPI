package dev.gtech.bifrost.bifrostapi.config.session;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;

import dev.gtech.bifrost.bifrostapi.config.settings.BifrostSettings;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoDBConnectionConfig extends AbstractMongoClientConfiguration {
    
    private final BifrostSettings settings;

    @Override
    protected String getDatabaseName() {
        return settings.getMongoConfig().getDatabaseName();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(new ConnectionString(settings.getMongoConfig().getUri()))
            .retryWrites(true)
            .retryReads(true)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    }
}
