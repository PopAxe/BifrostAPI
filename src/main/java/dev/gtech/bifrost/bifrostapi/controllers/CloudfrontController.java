package dev.gtech.bifrost.bifrostapi.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import dev.gtech.bifrost.bifrostapi.models.exceptions.BadRequestException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;

@Log4j2
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/api/v1/cloudfront")
public class CloudfrontController {

    private final CloudFrontUtilities cloudFrontUtilities;
    private final PrivateKey privateKey;

    @Value("${cloudfront.keypair-id}")
    private String keyPairId;
    private final List<String> allowedDomains = List.of(
        "gtech.dev",
        "cloudfront.net"
    );
    
    @GetMapping("/sign")
    public ModelAndView sign(
        @RequestParam(name = "resourceUrl") String resourceUrl,
        @AuthenticationPrincipal OidcUser user,
        HttpServletResponse response
    ) {
        URL uri;
        try {
            uri = new URL(resourceUrl);
        } catch (MalformedURLException e) {
            throw new BadRequestException("Bad Request");
        }

        log.info(String.format("Signing URI '%s' for user '%s'", uri.toString(), user.getPreferredUsername()));

        if (!allowedDomains.stream().anyMatch(domain -> uri.getHost().endsWith(domain))) {
            throw new BadRequestException("Bad Request");
        }

        /**
         * This is our way to parse the URI and determine if there's a path, we use that for signing.
         * Otherwise, we fallback to the root path so that resources underneath it are automatically captured.
         */
        String urlPath = String.format(
            "%s://%s/%s",
            uri.getProtocol(),
            uri.getHost(),
            StringUtils.isBlank(uri.getPath()) ? "" : uri.getPath()
        );

        CannedSignerRequest request = CannedSignerRequest.builder()
            .resourceUrl(urlPath)
            .keyPairId(keyPairId)
            .expirationDate(user.getExpiresAt())
            .privateKey(privateKey)
            .build();

         SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(request);

         return new ModelAndView(String.format("redirect:%s", signedUrl.url()));
    }
}
