package org.yuriy.timesheetservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetEntryRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetEntryResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface TimesheetService {
    TimesheetResponse createTimesheet(TimesheetCreateRequest req);

    List<TimesheetResponse> getTimesheetsByEmployee(Long employeeId);

    TimesheetResponse getTimesheetById(Long id);

    Page<TimesheetResponse> searchTimesheets(TimesheetSearchRequest request, Pageable pageable);

    TimesheetResponse approveTimesheet(Long id);

    TimesheetResponse rejectTimesheet(Long id);

    void markLeaveDays(Long employeeId, LocalDate startDate, LocalDate endDate, String type);

    @Transactional
    List<TimesheetEntryResponse> saveEntries(Long timesheetId, List<TimesheetEntryRequest> entries);

    @Transactional
    void deleteEntry(Long entryId);

    TimesheetSummaryResponse getWeeklySummary(Long employeeId);

    Page<TimesheetResponse> searchTimesheetsForManager(TimesheetSearchRequest req, Pageable pageable);
}
