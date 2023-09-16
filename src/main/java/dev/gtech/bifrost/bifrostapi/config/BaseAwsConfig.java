package dev.gtech.bifrost.bifrostapi.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Configuration
public class BaseAwsConfig {
    
    @Bean
    public Region getRegion() {
        if (StringUtils.isBlank(System.getenv("AWS_REGION"))) {
            return Region.US_WEST_2;
        }

        return Region.of(System.getenv("AWS_REGION"));
    }

    @Bean
    public AwsCredentialsProvider getAwsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }
}
