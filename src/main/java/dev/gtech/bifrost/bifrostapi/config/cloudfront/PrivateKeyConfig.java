package dev.gtech.bifrost.bifrostapi.config.cloudfront;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.gtech.bifrost.bifrostapi.config.settings.BifrostSettings;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PrivateKeyConfig {

    private final BifrostSettings settings;

    @Bean
    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return fromString(settings.getPrivateKey());
    }

    private PrivateKey fromString(String stringData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] data = Base64.getDecoder().decode(stringData.replace("-----BEGIN RSA PRIVATE KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END RSA PRIVATE KEY-----", ""));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
        return KeyFactory.getInstance("RSA", new BouncyCastleProvider()).generatePrivate(spec);
    }
}
