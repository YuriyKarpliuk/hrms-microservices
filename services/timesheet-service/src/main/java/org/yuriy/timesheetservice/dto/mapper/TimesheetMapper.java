package org.yuriy.timesheetservice.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetEntryResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.entity.Timesheet;
import org.yuriy.timesheetservice.entity.TimesheetEntry;
import org.yuriy.timesheetservice.entity.TimesheetStatus;
import org.yuriy.timesheetservice.service.EmployeeClient;

import java.util.ArrayList;
import java.util.List;


@Component
public class TimesheetMapper {
    private final EmployeeClient employeeClient;

    public TimesheetMapper(EmployeeClient employeeClient) {this.employeeClient = employeeClient;}

    public Timesheet toEntity(TimesheetCreateRequest req) {
        Timesheet ts = new Timesheet();
        ts.setEmployeeId(req.employeeId());
        ts.setWeekStart(req.weekStart());
        ts.setWeekEnd(req.weekEnd());
        ts.setStatus(TimesheetStatus.SUBMITTED);

        ts.setEntries(req.entries().stream().map(e -> {
            TimesheetEntry entry = new TimesheetEntry();
            entry.setWorkDate(e.workDate());
            entry.setActivityType(e.activityType());
            entry.setHours(e.hours());
            entry.setNotes(e.notes());
            entry.setTimesheet(ts);
            return entry;
        }).toList());

        return ts;
    }


    public TimesheetResponse toResponse(Timesheet ts) {
        var employee = employeeClient.getBasicInfo(ts.getEmployeeId());

        double totalHours = 0.0;
        List<TimesheetEntryResponse> entryResponses = new ArrayList<>();

        if (ts.getEntries() != null && !ts.getEntries().isEmpty()) {
            totalHours = ts.getEntries().stream()
                    .mapToDouble(e -> e.getHours() != null ? e.getHours() : 0.0)
                    .sum();

            entryResponses = ts.getEntries().stream()
                    .map(e -> new TimesheetEntryResponse(
                            e.getId(),
                            e.getWorkDate(),
                            e.getProject(),
                            e.getNotes(),
                            e.getHours(),
                            e.getActivityType()
                    ))
                    .toList();
        }

        return new TimesheetResponse(
                ts.getId(),
                ts.getEmployeeId(),
                employee.firstName(),
                employee.lastName(),
                ts.getWeekStart(),
                ts.getWeekEnd(),
                totalHours,
                ts.getStatus(),
                entryResponses
        );
    }}
