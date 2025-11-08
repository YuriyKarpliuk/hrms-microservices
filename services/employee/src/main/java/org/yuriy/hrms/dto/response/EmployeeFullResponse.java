package org.yuriy.hrms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmployeeFullResponse(
        Long id,
        String userId,
        String email,
        String firstName,
        String lastName,
        String position,
        String phone,
        Status status,
        Gender gender,
        MaritalStatus maritalStatus,
        String taxNumber,
        String about,
        String officeLocation,
        LocalDate birthDate,
        Integer age,
        LocalDate hiredAt,
        LocalDate terminatedAt,
        String avatarUrl,
        String cvKey,

        DepartmentInfo department,
        EmployeeShortInfo manager,
        EmployeeShortInfo hr,
        OrganizationInfo organization,

        JsonNode languages,
        JsonNode address,
        JsonNode education,
        JsonNode workExperience,
        JsonNode profile
) {

    public record DepartmentInfo(Long id, String name) {}
    public record EmployeeShortInfo(Long id, String firstName, String lastName, String email) {}
    public record OrganizationInfo(Long id, String name) {}

    public enum Gender {MALE, FEMALE}
    public enum MaritalStatus {SINGLE, MARRIED}
    public enum Status {ACTIVE, INACTIVE, ON_LEAVE, TERMINATED}
}
