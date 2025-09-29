package org.yuriy.hrms.dto.request;

import jakarta.validation.constraints.*;
import org.yuriy.hrms.entity.Employee;

import java.time.LocalDate;

public record EmployeePatchRequest(@Email String email, String firstName, String lastName, String phone,
        Employee.Status status, Long deptId, Long positionId, Long managerId, Long hrId, Employee.Gender gender,
        Employee.MaritalStatus maritalStatus, String taxNumber, String about, String officeLocation,
        LocalDate birthDate, LocalDate hiredAt, LocalDate terminatedAt, String avatarUrl) {
}
