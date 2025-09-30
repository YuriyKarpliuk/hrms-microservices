package org.yuriy.hrms.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.entity.Employee.MaritalStatus;
import org.yuriy.hrms.entity.Employee.Gender;

import java.time.LocalDate;

public record EmployeeCreateRequest(@NotNull Long orgId,
        Long deptId,
        Long positionId,
        Long managerId,
        Long hrId,

        String role,
        @Email @NotBlank String email,
        @NotBlank String firstName,
        @NotBlank String lastName,

        String phone,
        @NotNull Status status,
        Gender gender,
        MaritalStatus maritalStatus,
        String taxNumber,
        String about,
        String officeLocation,

        @Past(message = "birthDate must be in the past") LocalDate birthDate,
        @PastOrPresent LocalDate hiredAt,
        LocalDate terminatedAt,

        String avatarUrl,
        JsonNode languagesJson,
        JsonNode addressJson,
        JsonNode educationJson,
        JsonNode workExperienceJson,
        String cvKey,
        JsonNode profileJson) {
}

