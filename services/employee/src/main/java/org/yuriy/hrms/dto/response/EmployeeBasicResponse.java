package org.yuriy.hrms.dto.response;

import java.util.List;

public record EmployeeBasicResponse(Long id, String firstName, String lastName, String email, String position,
        List<String> roles) {
}
