package dev.gtech.bifrost.bifrostapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.PrivateKey;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import dev.gtech.bifrost.bifrostapi.config.settings.BifrostSettings;
import dev.gtech.bifrost.bifrostapi.config.vault.VaultConfig;
import dev.gtech.bifrost.bifrostapi.controllers.CloudfrontController;
import dev.gtech.bifrost.bifrostapi.controllers.UserController;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;


class BifrostApiApplicationTests {

    private Authentication auth;

    private OidcUser user;

    private BifrostSettings settings;

    private VaultConfig vaultConfig;

    private CloudfrontController cloudfrontController;

    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        settings = mock(BifrostSettings.class);
        vaultConfig = mock(VaultConfig.class);

        cloudfrontController = new CloudfrontController(CloudFrontUtilities.create(), mock(PrivateKey.class), settings);
        userController = new UserController();

        auth = mock(Authentication.class);
        user = mock(OidcUser.class);

        List<String> groups = List.of("group1");

        Map<String, Object> claims = Map.of(
            "claim1", "value1",
            "groups", groups
        );

        OidcUserInfo userInfo = OidcUserInfo.builder()
            .claim("claim1", "value1")
            .claim("groups",groups)
            .build();

        OidcIdToken token = OidcIdToken.withTokenValue("tokenvalue")
            .claim("claim1", "value1")
            .claim("groups", groups)
            .build();

        when(user.getUserInfo()).thenReturn(userInfo);
        when(user.getClaims()).thenReturn(claims);
        when(user.getClaim("groups")).thenReturn(groups);
        when(user.getClaimAsStringList("groups")).thenReturn(groups);
        when(user.getIdToken()).thenReturn(token);
        when(user.getPreferredUsername()).thenReturn("test-user");

        when(auth.getName()).thenReturn("test-user");

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testGetUserInfo() {
        assertTrue(userController.getUser(user).getUsername().equals("test-user"));
        assertEquals(userController.getUser(user).getIdToken(), "tokenvalue");
    } 

    @Test
    public void testGetUserGroups() {
        assertInstanceOf(List.class, userController.getUserGroups(user).getData(), "Groups is not of type list");
        assertEquals(List.of("group1"), userController.getUserGroups(user).getData());
        assertTrue(userController.getUserGroups(user).getData().equals(List.of("group1")));
    }

}
