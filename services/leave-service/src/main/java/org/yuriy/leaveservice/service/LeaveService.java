package org.yuriy.leaveservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yuriy.leaveservice.dto.request.LeaveCreateRequest;
import org.yuriy.leaveservice.dto.request.LeaveSearchRequest;
import org.yuriy.leaveservice.dto.response.LeaveResponse;

import java.util.List;

public interface LeaveService {
    LeaveResponse createLeave(LeaveCreateRequest req);

    List<LeaveResponse> getLeavesByEmployee(Long employeeId);

    LeaveResponse approveLeave(Long leaveId);

    LeaveResponse rejectLeave(Long leaveId);

    Page<LeaveResponse> searchLeaves(LeaveSearchRequest request, Pageable pageable);
}
