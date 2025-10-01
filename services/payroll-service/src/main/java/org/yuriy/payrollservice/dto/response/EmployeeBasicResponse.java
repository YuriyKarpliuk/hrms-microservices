package org.yuriy.payrollservice.dto.response;

public record EmployeeBasicResponse(Long id, String firstName, String lastName, String email, String position) {
}
