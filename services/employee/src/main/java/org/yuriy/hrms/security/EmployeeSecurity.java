package org.yuriy.hrms.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.yuriy.hrms.service.EmployeeService;

import java.util.Optional;

@Component("employeeSecurity")
@RequiredArgsConstructor
public class EmployeeSecurity {
    private final EmployeeService employeeService;

    public boolean isOwner(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("email");

        Optional<Object> employeeId = employeeService.findEmployeeIdByEmail(email);
        return employeeId.isPresent();
    }
}
