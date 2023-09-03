package dev.gtech.bifrost.bifrostapi.config.vault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Profile("dev")
public class DevVaultConfig extends AbstractVaultConfiguration {

    @Value("${vault.token}")
    private String token;

    @Value("${vault.hostname}")
    private String vaultHostname;

    @Value("${vault.port}")
    private int vaultPort;
    
    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(token);
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create(vaultHostname, vaultPort);
    }
}
