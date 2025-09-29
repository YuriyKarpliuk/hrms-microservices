package org.yuriy.hrms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.entity.Employee.MaritalStatus;
import org.yuriy.hrms.entity.Employee.Gender;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmployeeResponse(Long id, Long orgId, String userId, Long deptId, Long positionId, Long managerId,
        Long hrId, String email, String firstName, String lastName, String phone, Status status, Gender gender,
        MaritalStatus maritalStatus, String taxNumber, String about, String officeLocation, LocalDate birthDate,
        Integer age, LocalDate hiredAt, LocalDate terminatedAt, String avatarUrl, String cvKey) {
}
