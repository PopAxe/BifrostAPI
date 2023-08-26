package dev.gtech.bifrost.bifrostapi.controllers;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dev.gtech.bifrost.bifrostapi.models.common.GenericResult;
import dev.gtech.bifrost.bifrostapi.models.common.User;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @GetMapping("/info")
    @ResponseBody
    public User getUser(@AuthenticationPrincipal OidcUser user) {
        return User.fromOidcUser(user);
    }

    @GetMapping("/groups")
    @ResponseBody
    public GenericResult<List<String>> getUserGroups(@AuthenticationPrincipal OidcUser user) {
        return new GenericResult<>(user.getClaimAsStringList("group"));
    }

    @GetMapping("/token")
    public GenericResult<String> getIdToken(@AuthenticationPrincipal OidcUser user) {
        return new GenericResult<String>(user.getIdToken().getTokenValue());
    }
}
