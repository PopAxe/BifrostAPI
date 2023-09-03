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

    @Value("${vault.endpoint}")
    private String vaultEndpoint;
    
    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(token);
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        String vaultHostname = vaultEndpoint.split(":")[0];
        int vaultPort = Integer.valueOf(vaultEndpoint.split(":")[1]);
        return VaultEndpoint.create(vaultHostname, vaultPort);
    }
}
