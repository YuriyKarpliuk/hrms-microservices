package org.yuriy.hrms.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import org.yuriy.hrms.entity.Employee;

import java.time.LocalDate;

public record EmployeePatchRequest(@Email String email, String firstName, String lastName, String phone,
        Employee.Status status, Long orgId, Long deptId, String position, Long managerId, Long hrId, Employee.Gender gender,
        Employee.MaritalStatus maritalStatus, String taxNumber, String about, String officeLocation,
        LocalDate birthDate, LocalDate hiredAt, LocalDate terminatedAt, String avatarUrl,  JsonNode languagesJson,
        JsonNode addressJson,
        JsonNode educationJson,
        JsonNode workExperienceJson,
        JsonNode profileJson) {
}
