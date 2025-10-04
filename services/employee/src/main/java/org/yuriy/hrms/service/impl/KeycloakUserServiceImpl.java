package org.yuriy.hrms.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.yuriy.hrms.service.KeycloakUserService;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserServiceImpl implements KeycloakUserService {
    private final WebClient webClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakUserServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String createUser(String email, String firstName, String lastName, String roleName) {
        String accessToken = getAdminAccessToken();

        String userId = createUserInKeycloak(email, firstName, lastName, accessToken);

        sendPasswordSetupEmail(userId, accessToken);

        assignRealmRole(userId, roleName, accessToken);

        return userId;
    }

    private String getAdminAccessToken() {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "client_credentials")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret))
                .retrieve()
                .bodyToMono(Map.class)
                .map(map -> (String) map.get("access_token"))
                .block();
    }

    private String createUserInKeycloak(String email, String firstName, String lastName,
            String accessToken) {
        String userUrl = keycloakUrl + "/admin/realms/" + realm + "/users";

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("enabled", true);

        return webClient.post()
                .uri(userUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.CREATED) {
                        String location = response.headers().asHttpHeaders().getFirst(HttpHeaders.LOCATION);
                        assert location != null;
                        return Mono.just(location.substring(location.lastIndexOf("/") + 1));
                    }
                    return Mono.error(new RuntimeException("Failed to create user in Keycloak"));
                })
                .block();
    }

    private void sendPasswordSetupEmail(String userId, String accessToken) {
        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/execute-actions-email";

        webClient.put()
                .uri(url + "?lifespan=3600&redirect_uri=http://localhost:8222&client_id=hrms-rest-api")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of("UPDATE_PASSWORD"))
                .retrieve()
                .toBodilessEntity()
                .block();

    }

    private void assignRealmRole(String userId, String roleName, String accessToken) {
        String roleUrl = keycloakUrl + "/admin/realms/" + realm + "/roles/" + roleName;

        Map<String, Object> role = webClient.get()
                .uri(roleUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }

        String mappingUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";

        webClient.post()
                .uri(mappingUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Collections.singletonList(role))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void deleteUser(String userId) {
        String accessToken = getAdminAccessToken();

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void updateUser(String userId, String username, String email, String firstName, String lastName) {
        String accessToken = getAdminAccessToken();

        String url = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId;

        Map<String, Object> userUpdate = new HashMap<>();
        if (username != null)
            userUpdate.put("username", username);
        if (email != null)
            userUpdate.put("email", email);
        if (firstName != null)
            userUpdate.put("firstName", firstName);
        if (lastName != null)
            userUpdate.put("lastName", lastName);

        webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userUpdate)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public List<String> getUserRoles(String userId) {
        String accessToken = getAdminAccessToken();

        String rolesUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";

        return webClient.get()
                .uri(rolesUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(role -> (String) role.get("name"))
                .collectList()
                .block();
    }
}
