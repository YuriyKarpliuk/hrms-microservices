package org.yuriy.timesheetservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yuriy.timesheetservice.dto.request.TimesheetCreateRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetEntryRequest;
import org.yuriy.timesheetservice.dto.request.TimesheetSearchRequest;
import org.yuriy.timesheetservice.dto.response.TimesheetEntryResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetResponse;
import org.yuriy.timesheetservice.dto.response.TimesheetSummaryResponse;
import org.yuriy.timesheetservice.entity.TimesheetStatus;
import org.yuriy.timesheetservice.service.TimesheetService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/timesheets")
@Validated
public class TimesheetController {

    private final TimesheetService timesheetService;

    public TimesheetController(TimesheetService timesheetService) {this.timesheetService = timesheetService;}

    @PostMapping
    public ResponseEntity<TimesheetResponse> create(@Valid @RequestBody TimesheetCreateRequest req) {
        return ResponseEntity.ok(timesheetService.createTimesheet(req));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN') or @employeeSecurity.isOwner(authentication)")
    public ResponseEntity<List<TimesheetResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(timesheetService.getTimesheetsByEmployee(employeeId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN') or @employeeSecurity.isOwner(authentication)")
    public ResponseEntity<TimesheetResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(timesheetService.getTimesheetById(id));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN') or @employeeSecurity.isOwner(authentication)")
    public ResponseEntity<Page<TimesheetResponse>> searchTimesheets(
            @RequestBody TimesheetSearchRequest request,
            @PageableDefault(sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(timesheetService.searchTimesheets(request, pageable));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<TimesheetResponse> approveTimesheet(@PathVariable Long id) {
        return ResponseEntity.ok(timesheetService.approveTimesheet(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<TimesheetResponse> rejectTimesheet(@PathVariable Long id) {
        return ResponseEntity.ok(timesheetService.rejectTimesheet(id));
    }

    @PutMapping("/{id}/entries")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN') or @employeeSecurity.isOwner(authentication)")
    public ResponseEntity<List<TimesheetEntryResponse>> saveEntries(
            @PathVariable Long id,
            @RequestBody List<TimesheetEntryRequest> entries) {
        return ResponseEntity.ok(timesheetService.saveEntries(id, entries));
    }

    @DeleteMapping("/entries/{entryId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN') or @employeeSecurity.isOwner(authentication)")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long entryId) {
        timesheetService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{employeeId}/summary/week")
    public ResponseEntity<TimesheetSummaryResponse> getWeeklySummary(@PathVariable Long employeeId) {
        return ResponseEntity.ok(timesheetService.getWeeklySummary(employeeId));
    }
    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<TimesheetResponse>> getTeamTimesheets(
            @RequestParam Long managerId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEndTo,
            @PageableDefault Pageable pageable
    ) {
        TimesheetSearchRequest req = new TimesheetSearchRequest(
                employeeId,
                status != null ? TimesheetStatus.valueOf(status) : null,
                weekStartFrom,
                weekEndTo,
                managerId,
                employeeName
        );
        return ResponseEntity.ok(timesheetService.searchTimesheetsForManager(req, pageable));
    }


}
