package org.yuriy.leaveservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.yuriy.leaveservice.service.EmployeeClient;

@Component("employeeSecurity")
@RequiredArgsConstructor
public class EmployeeSecurity {
    private final EmployeeClient employeeClient;

    public boolean isOwner(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        Long employeeId = employeeClient.findEmployeeIdByEmail(email);
        return employeeId != null;
    }
}
