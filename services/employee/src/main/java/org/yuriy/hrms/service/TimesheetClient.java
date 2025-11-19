package org.yuriy.hrms.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.yuriy.hrms.dto.response.TimesheetResponse;

import java.time.LocalDate;
import java.util.Map;

@FeignClient(name = "timesheet-service")
public interface TimesheetClient {
    @GetMapping("/api/v1/timesheets/{employeeId}/summary/week")
    Map<String, Object> getWeeklySummary(@PathVariable Long employeeId);

    @GetMapping("/api/v1/timesheets/team")
    Page<TimesheetResponse> getTeamTimesheets(
            @RequestParam("managerId") Long managerId,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "employeeName", required = false) String employeeName,
            @RequestParam(value = "weekStartFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartFrom,
            @RequestParam(value = "weekEndTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndTo,
            Pageable pageable
    );
}
