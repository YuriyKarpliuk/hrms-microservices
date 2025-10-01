package org.yuriy.timesheetservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;

import java.util.List;

public interface TimesheetService {
    TimesheetResponse createTimesheet(TimesheetCreateRequest req);

    List<TimesheetResponse> getTimesheetsByEmployee(Long employeeId);

    TimesheetResponse getTimesheetById(Long id);

    Page<TimesheetResponse> searchTimesheets(TimesheetSearchRequest request, Pageable pageable);
}
