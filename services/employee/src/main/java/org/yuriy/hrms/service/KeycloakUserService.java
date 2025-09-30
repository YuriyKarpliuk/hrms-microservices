package org.yuriy.hrms.service;

public interface KeycloakUserService {
    String createUser(String email, String firstName, String lastName, String roleName);

    void deleteUser(String userId);

    void updateUser(String userId, String username, String email, String firstName, String lastName);
}
