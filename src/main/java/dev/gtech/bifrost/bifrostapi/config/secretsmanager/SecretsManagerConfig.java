package dev.gtech.bifrost.bifrostapi.config.secretsmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecretsManagerConfig {
    
    @Bean
    public SecretsManagerClient getSecretsManagerClient(Region region, AwsCredentialsProvider awsCredentialsProvider) {
        return SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(awsCredentialsProvider)
            .build();
    }
}
