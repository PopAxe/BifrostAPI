package dev.gtech.bifrost.bifrostapi.config.cloudfront;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;

@Configuration
public class CloudfrontSignerConfig {
    
    @Bean
    public CloudFrontUtilities getCloudfrontUtilities() {
        return CloudFrontUtilities.create();
    }
}
