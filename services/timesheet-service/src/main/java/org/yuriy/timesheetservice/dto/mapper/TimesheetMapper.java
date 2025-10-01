package org.yuriy.timesheetservice.dto.mapper;

import org.springframework.stereotype.Component;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetEntryResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.entity.ActivityType;
import org.yuriy.timesheetservice.entity.Timesheet;
import org.yuriy.timesheetservice.entity.TimesheetEntry;


@Component
public class TimesheetMapper {

    public Timesheet toEntity(TimesheetCreateRequest req) {
        Timesheet ts = new Timesheet();
        ts.setEmployeeId(req.employeeId());
        ts.setWeekStart(req.weekStart());
        ts.setWeekEnd(req.weekEnd());

        ts.setEntries(req.entries().stream().map(e -> {
            TimesheetEntry entry = new TimesheetEntry();
            entry.setWorkDate(e.workDate());
            entry.setActivityType(ActivityType.valueOf(e.activityType()));
            entry.setHours(e.hours());
            entry.setNotes(e.notes());
            entry.setTimesheet(ts);
            return entry;
        }).toList());

        return ts;
    }

    public TimesheetResponse toResponse(Timesheet ts) {
        return new TimesheetResponse(ts.getId(), ts.getEmployeeId(), ts.getWeekStart(), ts.getWeekEnd(),
                ts.getEntries().stream()
                        .map(e -> new TimesheetEntryResponse(e.getId(), e.getWorkDate(), e.getActivityType().name(),
                                e.getHours(), e.getNotes())).toList());
    }
}
