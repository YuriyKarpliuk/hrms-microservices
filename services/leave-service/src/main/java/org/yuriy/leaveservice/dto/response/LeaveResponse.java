package org.yuriy.leaveservice.dto.response;


import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;

import java.time.LocalDate;

public record LeaveResponse(Long id,
        Long employeeId,
        LeaveType type,
        LocalDate startDate,
        LocalDate endDate,
        LeaveStatus status,
        String reason) {
}
