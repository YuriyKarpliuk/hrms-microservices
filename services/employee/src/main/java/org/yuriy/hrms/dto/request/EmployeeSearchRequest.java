package org.yuriy.hrms.dto.request;

import org.yuriy.hrms.entity.Employee.Status;
import org.yuriy.hrms.entity.Employee.MaritalStatus;
import org.yuriy.hrms.entity.Employee.Gender;
import org.yuriy.hrms.repository.specification.EmployeeSpecification.StringMatchType;

import java.time.LocalDate;

public record EmployeeSearchRequest(String firstName, StringMatchType firstNameMatchType,
        String lastName, StringMatchType lastNameMatchType,
        String email, StringMatchType emailMatchType,
        Status status,
        Gender gender,
        MaritalStatus maritalStatus,
        LocalDate hiredFrom,
        LocalDate hiredTo,
        LocalDate birthFrom,
        LocalDate birthTo,
        String officeLocation,
        Long deptId,
        Long managerId,
        Long hrId,
        String phone) {
}
