package org.yuriy.hrms.dto.response;

public record ManagerTopPerformer(
        Long employeeId,
        String employeeName,
        double hours,
        boolean onLeave
) {}
