package org.yuriy.payrollservice.dto.response;

import java.time.LocalDate;

public record EmployeeBasicResponse(Long id, String firstName, String lastName, String email, String position,         LocalDate birthDate
        ) {
}
