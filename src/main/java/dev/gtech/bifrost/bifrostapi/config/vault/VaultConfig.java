package dev.gtech.bifrost.bifrostapi.config.vault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions.RoleId;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions.SecretId;
import org.springframework.vault.client.VaultClients;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Profile("!dev")
public class VaultConfig extends AbstractVaultConfiguration {

    @Value("${vault.roleId}")
    private String roleId;

    @Value("${vault.secretId}")
    private String secretId;
    
    @Override
    public ClientAuthentication clientAuthentication() {
        return new AppRoleAuthentication(
            AppRoleAuthenticationOptions.builder()
                .roleId(RoleId.provided(roleId))
                .secretId(SecretId.provided(secretId))
                .build(),
            VaultClients.createRestTemplate()
        );
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault", 8100);
    }
}
