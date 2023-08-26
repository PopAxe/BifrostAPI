package dev.gtech.bifrost.bifrostapi.models.common;

import java.util.List;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    
    private final String username;

    private final String email;

    private final boolean emailVerified;

    private final String fullName;

    private final List<String> groups;

    private final String idToken;

    public static User fromOidcUser(OidcUser user) {
        return User.builder()
            .username(user.getPreferredUsername())
            .email(user.getEmail())
            .emailVerified(user.getEmailVerified())
            .fullName(user.getFullName())
            .groups(user.getUserInfo().getClaimAsStringList("groups"))
            .idToken(user.getIdToken().getTokenValue())
            .build();
    }
}
