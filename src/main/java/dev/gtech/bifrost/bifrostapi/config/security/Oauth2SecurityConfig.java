package dev.gtech.bifrost.bifrostapi.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

import dev.gtech.bifrost.bifrostapi.config.settings.BifrostSettings;
import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Oauth2SecurityConfig {

    private final BifrostSettings settings;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.oauth2ClientRegistrationRepository());
    }

    @Bean 
    public ClientRegistration oauth2ClientRegistrationRepository() {
        return ClientRegistrations.fromOidcIssuerLocation(settings.getIssuerUri())
            .registrationId("oidc-client")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientName("OIDC Client")
            .clientId(settings.getClientId())
            .clientSecret(settings.getClientSecret())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope(List.of("openid", "groups", "profile", "email"))
            .build();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(exchanges ->
                exchanges
                    .requestMatchers("/", "/error").permitAll()
                    .anyRequest().authenticated()
              )
              .oauth2Login(withDefaults());
        return http.build();
    }
}
