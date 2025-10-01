package org.yuriy.leaveservice.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.leaveservice.dto.request.LeaveCreateRequest;
import org.yuriy.leaveservice.dto.response.LeaveResponse;
import org.yuriy.leaveservice.entity.Leave;
import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;


@Component
public class LeaveMapper {

    public Leave toEntity(LeaveCreateRequest r) {
        return Leave.builder()
                .employeeId(r.employeeId())
                .type(LeaveType.valueOf(r.type()))
                .startDate(r.startDate())
                .endDate(r.endDate())
                .status(LeaveStatus.PENDING)
                .reason(r.reason())
                .build();
    }

    public LeaveResponse toResponse(Leave l) {
        return new LeaveResponse(
                l.getId(),
                l.getEmployeeId(),
                l.getType(),
                l.getStartDate(),
                l.getEndDate(),
                l.getStatus(),
                l.getReason()
        );
    }
}
