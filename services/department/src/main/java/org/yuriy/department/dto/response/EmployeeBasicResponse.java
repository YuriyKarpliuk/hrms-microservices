package org.yuriy.department.dto.response;

import java.time.LocalDate;
import java.util.List;

public record EmployeeBasicResponse(Long id, String firstName, String lastName, String email, String position,
        LocalDate birthDate,
        List<String> roles) {
}
