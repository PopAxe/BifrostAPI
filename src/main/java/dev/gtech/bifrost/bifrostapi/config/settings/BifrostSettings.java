package dev.gtech.bifrost.bifrostapi.config.settings;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;

import lombok.Builder;
import lombok.Data;

@Configuration
@Data
public class BifrostSettings {

    @Autowired
    private VaultTemplate vaultTemplate;

    private MongoDBConfig mongoConfig;

    private String keypairId;
    private String privateKey;

    private String issuerUri;
    private String clientId;
    private String clientSecret;

    @PostConstruct
    public void init() {
        VaultVersionedKeyValueOperations ops = vaultTemplate.opsForVersionedKeyValue("/kv");
        Map<String, Object> envSettings = ops.get("/bifrost-api").getData();

        String mongoUsername = envSettings.get("mongoUsername").toString();
        String mongoPassword = envSettings.get("mongoPassword").toString();
        String mongoHost = envSettings.get("mongoHost").toString();
        String mongoDatabaseName = envSettings.get("mongoDatabaseName").toString();

        this.mongoConfig = MongoDBConfig.builder()
            .username(mongoUsername)
            .password(mongoPassword)
            .hostname(mongoHost)
            .databaseName(mongoDatabaseName)
            .build();

        this.keypairId = envSettings.get("keypairId").toString();
        this.privateKey = envSettings.get("cloudfrontPrivateKey").toString();

        this.issuerUri = envSettings.get("issuerUri").toString();
        this.clientId = envSettings.get("clientId").toString();
        this.clientSecret = envSettings.get("clientSecret").toString();
    }

    @Data
    @Builder
    public static class MongoDBConfig {
        private String username;
        private String password;
        private String hostname;
        private String databaseName;

        public String getUriWithoutDatabase() {
            return String.format("mongodb://%s:%s@%s", getUsername(), getPassword(), getHostname());
        }

        public String getUri() {
            return String.format("mongodb://%s:%s@%s/%s", getUsername(), getPassword(), getHostname(), getDatabaseName());
        }
    }
}
