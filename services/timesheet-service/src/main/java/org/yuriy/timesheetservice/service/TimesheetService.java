package org.yuriy.timesheetservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;

import java.util.List;

public interface TimesheetService {
    TimesheetResponse createTimesheet(TimesheetCreateRequest req);

    List<TimesheetResponse> getTimesheetsByEmployee(Long employeeId);

    TimesheetResponse getTimesheetById(Long id);

    Page<TimesheetResponse> searchTimesheets(TimesheetSearchRequest request, Pageable pageable);

    @Transactional
    TimesheetResponse approveTimesheet(Long id);

    @Transactional
    TimesheetResponse rejectTimesheet(Long id);
}
