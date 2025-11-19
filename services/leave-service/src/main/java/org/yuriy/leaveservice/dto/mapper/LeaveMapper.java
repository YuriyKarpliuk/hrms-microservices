package org.yuriy.leaveservice.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.leaveservice.dto.request.LeaveCreateRequest;
import org.yuriy.leaveservice.dto.response.EmployeeBasicResponse;
import org.yuriy.leaveservice.dto.response.LeaveResponse;
import org.yuriy.leaveservice.entity.Leave;
import org.yuriy.leaveservice.entity.LeaveStatus;
import org.yuriy.leaveservice.entity.LeaveType;
import org.yuriy.leaveservice.service.EmployeeClient;


@Component
public class LeaveMapper {

    private final EmployeeClient employeeClient;

    public LeaveMapper(EmployeeClient employeeClient) {this.employeeClient = employeeClient;}

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
        EmployeeBasicResponse emp = employeeClient.getBasicInfo(l.getEmployeeId());

        return new LeaveResponse(
                l.getId(),
                l.getEmployeeId(),
                emp.firstName(),
                emp.lastName(),
                l.getType(),
                l.getStartDate(),
                l.getEndDate(),
                l.getStatus(),
                l.getReason()
        );
    }
}
