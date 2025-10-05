package org.yuriy.notificationservice.kafka;


import java.time.LocalDate;
import java.util.List;

public record EmployeeCreatedEvent(Long id,
        Long orgId,
        Long deptId,
        String email,
        List<String> role,
        String firstName,
        String lastName,
        String position,
        LocalDate hiredAt,
        String status) {
}
