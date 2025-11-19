package org.yuriy.notificationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ServiceTokenProvider {

    private final WebClient webClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private String cachedToken;
    private long expiresAt = 0;

    public ServiceTokenProvider(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    public synchronized String getToken() {

        if (cachedToken != null && System.currentTimeMillis() < expiresAt) {
            return cachedToken;
        }
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        Map response = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(
                        BodyInserters.fromFormData("grant_type", "client_credentials")
                                .with("client_id", clientId)
                                .with("client_secret", clientSecret)
                )
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        cachedToken = (String) response.get("access_token");
        Integer expiresIn = (Integer) response.get("expires_in");

        expiresAt = System.currentTimeMillis() + (expiresIn - 10) * 1000L;

        return cachedToken;
    }
}
