package dev.gtech.bifrost.bifrostapi.config.cloudfront;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Log4j2
public class PrivateKeyConfig {
    private final SecretsManagerClient secretsManagerClient;

    @Bean
    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyString = secretsManagerClient.getSecretValue(
            GetSecretValueRequest.builder()
                .secretId("cloudfront-private-key")
                .build()
        ).secretString();

        return stringToPrivateKey(privateKeyString);
    }

    private PrivateKey stringToPrivateKey(String stringData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String sanitizedPrivateKey = stringData.replace("-----BEGIN PRIVATE KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PRIVATE KEY-----", "");
        byte[] data = Base64.getDecoder().decode((sanitizedPrivateKey.getBytes()));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("RSA");
        return fact.generatePrivate(spec);
    }
}
