package org.yuriy.leaveservice.dto.request;


import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;

import java.time.LocalDate;

public record LeaveSearchRequest(Long employeeId, LeaveStatus status, LeaveType type, LocalDate startFrom,
        LocalDate startTo, LocalDate endFrom, LocalDate endTo) {
}
