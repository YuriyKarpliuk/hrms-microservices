package org.yuriy.hrms.kafka;


import java.time.LocalDate;

public record EmployeeCreatedEvent(Long id,
        Long orgId,
        Long deptId,
        String email,
        String firstName,
        String lastName,
        String position,
        LocalDate hiredAt,
        String status) {
}
