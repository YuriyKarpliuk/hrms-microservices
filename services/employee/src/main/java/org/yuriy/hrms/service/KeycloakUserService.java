package org.yuriy.hrms.service;

import java.util.List;

public interface KeycloakUserService {
    String createUser(String email, String firstName, String lastName, String roleName);

    void deleteUser(String userId);

    void updateUser(String userId, String username, String email, String firstName, String lastName);

    List<String> getUserRoles(String userId);
}
